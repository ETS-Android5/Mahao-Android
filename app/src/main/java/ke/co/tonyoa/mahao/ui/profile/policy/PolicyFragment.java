package ke.co.tonyoa.mahao.ui.profile.policy;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.databinding.FragmentPolicyBinding;

public class PolicyFragment extends BaseFragment {

    private String mUrl;
    private int mTitleId;

    public PolicyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments()!=null){
            PolicyFragmentArgs policyFragmentArgs = PolicyFragmentArgs.fromBundle(getArguments());
            mUrl = policyFragmentArgs.getUrl();
            mTitleId = policyFragmentArgs.getTitle();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ke.co.tonyoa.mahao.databinding.FragmentPolicyBinding fragmentPolicyBinding = FragmentPolicyBinding.inflate(inflater, container, false);
        setToolbar(fragmentPolicyBinding.layoutToolbar.materialToolbarLayoutToolbar);
        setTitle(getString(mTitleId));

        fragmentPolicyBinding.webViewPolicy.getSettings().setJavaScriptEnabled(true);
        fragmentPolicyBinding.webViewPolicy.loadUrl(mUrl);

        return fragmentPolicyBinding.getRoot();
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