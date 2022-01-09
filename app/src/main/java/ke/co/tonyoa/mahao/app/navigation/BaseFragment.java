package ke.co.tonyoa.mahao.app.navigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import ke.co.tonyoa.mahao.R;

public abstract class BaseFragment extends Fragment {

    private NavController mNavController;
    private Toolbar mToolbar;

    protected final void setToolbar(Toolbar toolbar) {
        mToolbar = toolbar;
        NavigationUI.setupWithNavController(mToolbar, getNavController());
        ((AppCompatActivity)requireActivity()).setSupportActionBar(toolbar);
    }

    protected final void setTitle(String title) {
        if (mToolbar != null) {
            mToolbar.setTitle(title);
        }
    }

    protected final void navigate(NavDirections navDirections) {
        getNavController().navigate(navDirections);
    }

    protected final void navigateUp() {
        getNavController().navigateUp();
    }

    protected final void navigateBack() {
        getNavController().popBackStack();
    }

    protected final NavController getNavController() {
        if (mNavController == null) {
            mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        }
        return mNavController;
    }

}