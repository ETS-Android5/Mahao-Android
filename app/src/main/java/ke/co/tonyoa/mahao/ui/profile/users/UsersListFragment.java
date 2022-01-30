package ke.co.tonyoa.mahao.ui.profile.users;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.paging.RepoDataSource;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentUsersListBinding;

public class UsersListFragment extends BaseFragment {

    private FragmentUsersListBinding mFragmentUsersListBinding;
    private UsersListViewModel mUsersListViewModel;
    private UserAdapter mUserAdapter;

    public UsersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mUsersListViewModel = new ViewModelProvider(this).get(UsersListViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentUsersListBinding = FragmentUsersListBinding.inflate(inflater, container, false);
        setToolbar(mFragmentUsersListBinding.layoutToolbar.materialToolbarLayoutToolbar);
        setTitle(getString(R.string.users));

        mUserAdapter = new UserAdapter(requireContext(), (user, position) -> {
            navigate(UsersListFragmentDirections.actionUsersListFragmentToViewUserFragment(user));
        });
        mFragmentUsersListBinding.recyclerViewUsersList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        mFragmentUsersListBinding.recyclerViewUsersList.setAdapter(mUserAdapter);
        fetchData();
        mFragmentUsersListBinding.swipeRefreshLayoutUsers.setOnRefreshListener(()-> mUsersListViewModel.invalidateList());

        return mFragmentUsersListBinding.getRoot();
    }

    private void fetchData(){
        LinearLayout loadingView = mFragmentUsersListBinding.linearLayoutUsersListLoading;
        List<RecyclerView> enabledViews = Arrays.asList(mFragmentUsersListBinding.recyclerViewUsersList);
        ViewUtils.load(loadingView, enabledViews, true);
        mUsersListViewModel.getUsers().observe(getViewLifecycleOwner(), userPagedList ->{
            mUserAdapter.submitList(userPagedList);
            if (userPagedList.getLoadedCount()>0){
                mFragmentUsersListBinding.recyclerViewUsersList.setVisibility(View.VISIBLE);
                mFragmentUsersListBinding.linearLayoutUsersListEmpty.setVisibility(View.GONE);
            }
            else {
                mFragmentUsersListBinding.recyclerViewUsersList.setVisibility(View.GONE);
                mFragmentUsersListBinding.linearLayoutUsersListEmpty.setVisibility(View.VISIBLE);
            }
        });
        mUsersListViewModel.getLoadState().observe(getViewLifecycleOwner(), loadState->{
            if (loadState != RepoDataSource.LOADING_ONGOING){
                mFragmentUsersListBinding.swipeRefreshLayoutUsers.setRefreshing(false);
                mFragmentUsersListBinding.linearProgressIndicatorUsersList.setVisibility(View.GONE);
            }
            else {
                mFragmentUsersListBinding.linearProgressIndicatorUsersList.setVisibility(View.VISIBLE);
            }

            // No items loaded, and it is loading
            ViewUtils.load(loadingView, enabledViews, mUserAdapter.getItemCount() == 0 && loadState == RepoDataSource.LOADING_ONGOING);
            mUserAdapter.setLoadState(loadState);
        });
        mUsersListViewModel.getErrors().observe(getViewLifecycleOwner(), error->{
            if (error != null) {
                // Show any errors other than one showing no next page found
                if (!error.equalsIgnoreCase("Not Found") || mUserAdapter.getItemCount() == 0)
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}