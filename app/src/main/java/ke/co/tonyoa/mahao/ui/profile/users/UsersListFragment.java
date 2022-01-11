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
        mFragmentUsersListBinding.swipeRefreshLayoutUsers.setOnRefreshListener(this::fetchData);

        return mFragmentUsersListBinding.getRoot();
    }

    private void fetchData(){
        LinearLayout loadingView = mFragmentUsersListBinding.linearLayoutUsersListLoading;
        List<RecyclerView> enabledViews = Arrays.asList(mFragmentUsersListBinding.recyclerViewUsersList);
        ViewUtils.load(loadingView, enabledViews, true);
        mUsersListViewModel.getUsers().observe(getViewLifecycleOwner(), listAPIResponse -> {
            ViewUtils.load(loadingView, enabledViews, false);
            int usersCount = 0;
            if (listAPIResponse!=null && listAPIResponse.isSuccessful()){
                List<User> users = listAPIResponse.body();
                mUserAdapter.submitList(users);
                if (users!=null)
                    usersCount = users.size();
            }
            else {
                Toast.makeText(requireContext(),
                        (listAPIResponse==null || listAPIResponse.errorMessage(requireContext())==null)?
                                getString(R.string.unknown_error):
                                listAPIResponse.errorMessage(requireContext()),
                        Toast.LENGTH_SHORT).show();
                usersCount = mUserAdapter.getItemCount();
            }

            if (usersCount>0){
                mFragmentUsersListBinding.linearLayoutUsersListEmpty.setVisibility(View.GONE);
                mFragmentUsersListBinding.recyclerViewUsersList.setVisibility(View.VISIBLE);
            }
            else {
                mFragmentUsersListBinding.linearLayoutUsersListEmpty.setVisibility(View.VISIBLE);
                mFragmentUsersListBinding.recyclerViewUsersList.setVisibility(View.GONE);
            }
            mFragmentUsersListBinding.swipeRefreshLayoutUsers.setRefreshing(false);
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