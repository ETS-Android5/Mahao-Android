package ke.co.tonyoa.mahao.ui.properties.single;

import static ke.co.tonyoa.mahao.ui.properties.single.SinglePropertyFragment.PROPERTY_EXTRA;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dev.ronnie.github.imagepicker.ImagePicker;
import dev.ronnie.github.imagepicker.ImageResult;
import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.api.responses.PropertyAmenity;
import ke.co.tonyoa.mahao.app.api.responses.PropertyPhoto;
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.enums.FeedbackType;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.SnapScrollListener;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentViewPropertyBinding;
import ke.co.tonyoa.mahao.databinding.LayoutSomeHousesBinding;
import ke.co.tonyoa.mahao.ui.profile.amenities.AmenityAdapter;
import ke.co.tonyoa.mahao.ui.properties.PropertyAdapter;
import ke.co.tonyoa.mahao.ui.properties.PropertyPhotoAdapter;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ViewPropertyFragment extends BaseFragment implements PropertyAmenitySelectFragment.OnPropertyAmenitiesChangedListener {

    private Property mProperty;
    private FragmentViewPropertyBinding mFragmentViewPropertyBinding;
    private SinglePropertyViewModel mSinglePropertyViewModel;
    private AmenityAdapter mAmenityAdapter;
    private PropertyAdapter mPropertyAdapterRecommended;
    private PropertyPhotoAdapter mPropertyPhotoAdapter;
    private ImagePicker mImagePicker;

    public ViewPropertyFragment() {
        // Required empty public constructor
    }

    public static ViewPropertyFragment newInstance(Property property) {
        ViewPropertyFragment fragment = new ViewPropertyFragment();
        Bundle args = new Bundle();
        args.putSerializable(PROPERTY_EXTRA, property);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mProperty = (Property) getArguments().getSerializable(PROPERTY_EXTRA);
        }
        mSinglePropertyViewModel = new ViewModelProvider(requireParentFragment()).get(SinglePropertyViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentViewPropertyBinding = FragmentViewPropertyBinding.inflate(inflater, container, false);
        setToolbar(mFragmentViewPropertyBinding.layoutToolbar.materialToolbarLayoutToolbar);
        if (mProperty==null){
            setTitle("New Property");
        }
        else {
            setTitle(mProperty.getTitle());
        }
        mImagePicker = new ImagePicker(this);

        if (mProperty != null) {
            mSinglePropertyViewModel.addFeedback(mProperty.getId(), FeedbackType.READ);

            Glide.with(requireContext())
                    .load(mProperty.getFeatureImage())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mFragmentViewPropertyBinding.imageViewViewPropertyFeature);

            mFragmentViewPropertyBinding.textViewViewPropertyAddress.setText(mProperty.getLocationName());
            mFragmentViewPropertyBinding.textViewViewPropertyTitle.setText(mProperty.getTitle());
            mFragmentViewPropertyBinding.textViewViewPropertyPrice.setText(getString(R.string.kes_f_month, mProperty.getPrice()));
            displayFavorite(false);
            mFragmentViewPropertyBinding.imageViewViewPropertyVerified.setVisibility(mProperty.getIsVerified()?View.VISIBLE:View.GONE);
            mFragmentViewPropertyBinding.imageViewViewPropertyEnabled.setImageTintList(ColorStateList.valueOf(mProperty.getIsEnabled()?
                    Color.GREEN:Color.RED));
            mFragmentViewPropertyBinding.textViewViewPropertyCategory.setText(mProperty.getPropertyCategory().getTitle());

            User owner = mProperty.getOwner();
            Glide.with(requireContext())
                    .load(owner.getProfilePicture())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
                    .transform(new CircleCrop())
                    .into(mFragmentViewPropertyBinding.imageViewViewPropertyOwner);
            mFragmentViewPropertyBinding.textViewViewPropertyOwnerName.setText(String.format("%s %s",
                    owner.getFirstName(), owner.getLastName()));
            mFragmentViewPropertyBinding.imageButtonViewPropertyText.setOnClickListener(v->{
                mSinglePropertyViewModel.addFeedback(mProperty.getId(), FeedbackType.TEXT);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey, saw one of your posts on Mahao that I like, "+
                        mProperty.getTitle());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

            });
            mFragmentViewPropertyBinding.imageButtonViewPropertyCall.setOnClickListener(v->{
                mSinglePropertyViewModel.addFeedback(mProperty.getId(), FeedbackType.CALL);
                Uri phoneUri = Uri.parse("tel:" + mProperty.getOwner().getPhone());
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, phoneUri);
                try {
                    // Launch the Phone app's dialer with a phone number
                    startActivity(phoneIntent);
                }
                catch (SecurityException s) {
                    Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_LONG).show();
                }
            });
            mFragmentViewPropertyBinding.textViewViewPropertyBeds.setText(getString(R.string.d_beds,
                    mProperty.getNumBed()));
            mFragmentViewPropertyBinding.textViewViewPropertyBaths.setText(getString(R.string.d_baths,
                    mProperty.getNumBath()));
            mFragmentViewPropertyBinding.textViewViewPropertyAmenities.setText(getString(R.string.d_amens,
                    mProperty.getPropertyAmenities().size()));

            String text = "<html><head>"
                    + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #868686;text-align:left;font-size:14px;margin-left:0px;line-height:1.8}"
                    + "</style></head>"
                    + "<body>"
                    + mProperty.getDescription()
                    + "</body></html>";

            String mimeType = "text/html";
            String encoding = "utf-8";
            mFragmentViewPropertyBinding.webViewViewPropertyDescription.loadDataWithBaseURL(null, text,
                    mimeType, encoding, null);
            mFragmentViewPropertyBinding.webViewViewPropertyDescription.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.transparent,
                            null));
                }
            });

            mAmenityAdapter = new AmenityAdapter(requireContext(), null);
            mFragmentViewPropertyBinding.recyclerViewViewPropertyAmenities.setLayoutManager(new LinearLayoutManager(requireContext(),
                    RecyclerView.HORIZONTAL, false));
            mFragmentViewPropertyBinding.recyclerViewViewPropertyAmenities.setAdapter(mAmenityAdapter);
            List<Amenity> amenityList = new ArrayList<>();
            for (PropertyAmenity propertyAmenity:mProperty.getPropertyAmenities()){
                amenityList.add(propertyAmenity.getAmenity());
            }
            mAmenityAdapter.submitList(amenityList);
            mFragmentViewPropertyBinding.linearLayoutViewPropertyEmptyAmenities.setVisibility(amenityList.size()>0?View.GONE:View.VISIBLE);

            mPropertyPhotoAdapter = new PropertyPhotoAdapter(requireContext(), (propertyPhoto, position) -> {
                List<String> propertyPhotos = new ArrayList<>();
                for (PropertyPhoto single:mPropertyPhotoAdapter.getCurrentList()){
                    propertyPhotos.add(single.getPhoto());
                }
                navigate(SinglePropertyFragmentDirections
                        .actionSinglePropertyFragmentToFullPhotoFragment(propertyPhotos.toArray(new String[0]), propertyPhoto.getPhoto()));
            }, (propertyPhoto, position) -> {
                List<View> enabledViews = Arrays.asList(mFragmentViewPropertyBinding.recyclerViewViewPropertyGallery,
                        mFragmentViewPropertyBinding.buttonViewPropertyAddImage);
                ViewUtils.load(mFragmentViewPropertyBinding.linearLayoutViewPropertyLoadingGallery,
                        enabledViews, true);
                mSinglePropertyViewModel.removePropertyPhoto(mProperty.getId(), propertyPhoto.getId()).observe(getViewLifecycleOwner(), propertyPhotoAPIResponse -> {
                    ViewUtils.load(mFragmentViewPropertyBinding.linearLayoutViewPropertyLoadingGallery,
                            enabledViews, false);
                    if (propertyPhotoAPIResponse!=null && propertyPhotoAPIResponse.isSuccessful()){
                        List<PropertyPhoto> currentPropertyPhotos = mPropertyPhotoAdapter.getCurrentList();
                        List<PropertyPhoto> newPropertyPhotos = new ArrayList<>(currentPropertyPhotos);
                        newPropertyPhotos.remove(propertyPhotoAPIResponse.body());
                        mPropertyPhotoAdapter.submitList(newPropertyPhotos);
                        mFragmentViewPropertyBinding.linearLayoutViewPropertyEmptyGallery.setVisibility(newPropertyPhotos.size()>0?View.GONE:View.VISIBLE);
                    }
                    else {
                        Toast.makeText(requireContext(),
                                (propertyPhotoAPIResponse==null || propertyPhotoAPIResponse.errorMessage(requireContext())==null)?
                                        getString(R.string.unknown_error):
                                        propertyPhotoAPIResponse.errorMessage(requireContext()),
                                Toast.LENGTH_SHORT).show();
                    }

                });
            },  Objects.equals(mSinglePropertyViewModel.getUserId(), mProperty.getOwnerId()+""));

            mFragmentViewPropertyBinding.recyclerViewViewPropertyGallery.setLayoutManager(new LinearLayoutManager(requireContext(),
                    RecyclerView.HORIZONTAL, false));
            mFragmentViewPropertyBinding.recyclerViewViewPropertyGallery.setAdapter(mPropertyPhotoAdapter);
            mPropertyPhotoAdapter.submitList(mProperty.getPropertyPhotos());
            mFragmentViewPropertyBinding.linearLayoutViewPropertyEmptyGallery.setVisibility(mProperty.getPropertyPhotos().size()>0?View.GONE:View.VISIBLE);

            mFragmentViewPropertyBinding.buttonViewPropertyAddImage.setOnClickListener(v->{
                pickOrTakeImage();
            });

            mFragmentViewPropertyBinding.buttonViewPropertyEditAmenities.setOnClickListener(v->{
                PropertyAmenitySelectFragment.show(getChildFragmentManager(), mProperty.getId(),
                        new ArrayList<>(mAmenityAdapter.getCurrentList()),
                        this);
            });


            mFragmentViewPropertyBinding.layoutSomeHousesRecommended.textViewSomeHousesShowMore.setVisibility(View.GONE);
            mFragmentViewPropertyBinding.layoutSomeHousesRecommended.textViewSomeHousesTitle.setText(R.string.related);
            RecyclerView recyclerViewSomeHouses = mFragmentViewPropertyBinding.layoutSomeHousesRecommended.recyclerViewSomeHouses;
            mPropertyAdapterRecommended = new PropertyAdapter(PropertyAdapter.ListType.HORIZONTAL_PROPERTY,
                    requireContext(), (property, position) -> {
                        navigate(SinglePropertyFragmentDirections.actionSinglePropertyFragmentSelf(property));
                    },  (property, position) -> {
                        mSinglePropertyViewModel.addFavorite(mProperty.getIsFavorite(), property.getId()).observe(getViewLifecycleOwner(), favoriteResponseAPIResponse -> {
                            if (favoriteResponseAPIResponse == null || !favoriteResponseAPIResponse.isSuccessful()) {
                                Toast.makeText(requireContext(),
                                        (favoriteResponseAPIResponse==null || favoriteResponseAPIResponse.errorMessage(requireContext())==null)?
                                                getString(R.string.unknown_error):
                                                favoriteResponseAPIResponse.errorMessage(requireContext()),
                                        Toast.LENGTH_SHORT).show();
                                property.setIsFavorite(!property.getIsFavorite());
                                mPropertyAdapterRecommended.notifyItemChanged(position, Arrays.asList("like"));
                            }
                        });
                    }, (property, position)->{
                        mSinglePropertyViewModel.addFeedback(property.getId(), FeedbackType.READ);
                    });
            recyclerViewSomeHouses
                    .setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewSomeHouses.setAdapter(mPropertyAdapterRecommended);

            PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
            pagerSnapHelper.attachToRecyclerView(recyclerViewSomeHouses);
            TabLayout tabLayout = mFragmentViewPropertyBinding.layoutSomeHousesRecommended.tabLayoutSomeHouses;
            recyclerViewSomeHouses.addOnScrollListener(new SnapScrollListener(pagerSnapHelper,
                    SnapScrollListener.Behavior.NOTIFY_ON_SCROLL,
                    position -> {
                        tabLayout.selectTab(tabLayout.getTabAt(position));
                    }));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    recyclerViewSomeHouses.scrollToPosition(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            fetchSingleRow(mFragmentViewPropertyBinding.layoutSomeHousesRecommended,
                    mSinglePropertyViewModel.getSimilarProperties(mProperty.getId()), mPropertyAdapterRecommended);

            mFragmentViewPropertyBinding.floatingActionButtonViewPropertyMap.setOnClickListener(v->{
                mSinglePropertyViewModel.addFeedback(mProperty.getId(), FeedbackType.MAP);
                navigate(SinglePropertyFragmentDirections.actionSinglePropertyFragmentToPropertyMapFragment(new float[]{mProperty.getLocation().get(0),
                    mProperty.getLocation().get(1), 10}, mProperty.getId()));
            });

            mFragmentViewPropertyBinding.animationViewViewPropertyLike.setOnClickListener(v->{
                mProperty.setIsFavorite(!mProperty.getIsFavorite());
                displayFavorite(true);
                LottieAnimationView loadingView = null;
                List<View> enabledViews = Arrays.asList(mFragmentViewPropertyBinding.animationViewViewPropertyLike);
                ViewUtils.load(loadingView, enabledViews, true);
                mSinglePropertyViewModel.addFavorite(mProperty.getIsFavorite(), mProperty.getId()).observe(getViewLifecycleOwner(), favoriteResponseAPIResponse -> {
                    ViewUtils.load(loadingView, enabledViews, false);
                    if (favoriteResponseAPIResponse == null || !favoriteResponseAPIResponse.isSuccessful()) {
                        Toast.makeText(requireContext(),
                                (favoriteResponseAPIResponse==null || favoriteResponseAPIResponse.errorMessage(requireContext())==null)?
                                        getString(R.string.unknown_error):
                                        favoriteResponseAPIResponse.errorMessage(requireContext()),
                                Toast.LENGTH_SHORT).show();
                        mProperty.setIsFavorite(!mProperty.getIsFavorite());
                        displayFavorite(true);
                    }
                });
            });
        }

        if ((!mSinglePropertyViewModel.isAdmin() && (mProperty!=null && !mProperty.getOwnerId().equals(Integer.parseInt(mSinglePropertyViewModel.getUserId()))))) {
            mFragmentViewPropertyBinding.buttonViewPropertyEditAmenities.setVisibility(View.GONE);
            mFragmentViewPropertyBinding.buttonViewPropertyAddImage.setVisibility(View.GONE);
        }
        else {
            mFragmentViewPropertyBinding.buttonViewPropertyEditAmenities.setVisibility(View.VISIBLE);
            mFragmentViewPropertyBinding.buttonViewPropertyAddImage.setVisibility(View.VISIBLE);
        }


        return mFragmentViewPropertyBinding.getRoot();
    }

    private void fetchSingleRow(LayoutSomeHousesBinding layoutSomeHousesBinding, LiveData<APIResponse<List<Property>>> apiResponseLiveData,
                                PropertyAdapter propertyAdapter){
        LottieAnimationView loadingView = layoutSomeHousesBinding.animationViewSomeHousesLoading;
        List<RecyclerView> enabledViews = Arrays.asList(layoutSomeHousesBinding.recyclerViewSomeHouses);
        ViewUtils.load(loadingView, enabledViews, true);
        apiResponseLiveData.observe(getViewLifecycleOwner(), listAPIResponse -> {
            ViewUtils.load(loadingView, enabledViews, false);
            int propertyCount = 0;
            if (listAPIResponse!=null && listAPIResponse.isSuccessful()){
                List<Property> properties = listAPIResponse.body();
                propertyAdapter.submitList(properties);
                if (properties!=null) {
                    propertyCount = properties.size();

                    //Add tabs
                    layoutSomeHousesBinding.tabLayoutSomeHouses.removeAllTabs();
                    for (int index = 0; index<propertyCount; index++) {
                        TabLayout.Tab newTab = layoutSomeHousesBinding.tabLayoutSomeHouses.newTab();
                        layoutSomeHousesBinding.tabLayoutSomeHouses.addTab(newTab);
                    }
                }
            }
            else {
                Toast.makeText(requireContext(),
                        (listAPIResponse==null || listAPIResponse.errorMessage(requireContext())==null)?
                                getString(R.string.unknown_error):
                                listAPIResponse.errorMessage(requireContext()),
                        Toast.LENGTH_SHORT).show();
                propertyCount = propertyAdapter.getItemCount();
            }

            layoutSomeHousesBinding.getRoot().setVisibility(propertyCount>0?View.VISIBLE:View.GONE);
        });
    }


    private void displayFavorite(boolean click){
        if (click) {
            if (mProperty.getIsFavorite()) {
                mFragmentViewPropertyBinding.animationViewViewPropertyLike.setProgress(0);
                mFragmentViewPropertyBinding.animationViewViewPropertyLike.setSpeed(1);
            } else {
                mFragmentViewPropertyBinding.animationViewViewPropertyLike.setProgress(1);
                mFragmentViewPropertyBinding.animationViewViewPropertyLike.setSpeed(-1);
            }
            mFragmentViewPropertyBinding.animationViewViewPropertyLike.playAnimation();
        }
        else {
            mFragmentViewPropertyBinding.animationViewViewPropertyLike.setProgress(mProperty.getIsFavorite()?1:0);
        }
    }

    private void pickOrTakeImage(){
        Function1<ImageResult<? extends Uri>, Unit> imageCallback = imageResult -> {
            if (imageResult instanceof ImageResult.Success) {
                Uri uri = ((ImageResult.Success<Uri>) imageResult).getValue();
                List<View> enabledViews = Arrays.asList(mFragmentViewPropertyBinding.buttonViewPropertyAddImage,
                        mFragmentViewPropertyBinding.recyclerViewViewPropertyGallery);
                ViewUtils.load(mFragmentViewPropertyBinding.linearLayoutViewPropertyLoadingGallery,
                        enabledViews, true);
                mSinglePropertyViewModel.addPropertyPhotos(mProperty.getId(), Arrays.asList(uri)).observe(getViewLifecycleOwner(), listAPIResponse -> {
                    ViewUtils.load(mFragmentViewPropertyBinding.linearLayoutViewPropertyLoadingGallery,
                            enabledViews, false);
                    if (listAPIResponse!=null && listAPIResponse.isSuccessful()){
                        List<PropertyPhoto> currentPropertyPhotos = mPropertyPhotoAdapter.getCurrentList();
                        List<PropertyPhoto> newPropertyPhotos = new ArrayList<>(currentPropertyPhotos);
                        newPropertyPhotos.addAll(listAPIResponse.body());
                        mPropertyPhotoAdapter.submitList(newPropertyPhotos);
                        mFragmentViewPropertyBinding.linearLayoutViewPropertyEmptyGallery
                                .setVisibility(newPropertyPhotos.size()>0?View.GONE:View.VISIBLE);
                    }
                    else {
                        Toast.makeText(requireContext(),
                                (listAPIResponse==null || listAPIResponse.errorMessage(requireContext())==null)?
                                        getString(R.string.unknown_error):
                                        listAPIResponse.errorMessage(requireContext()),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                String errorString = ((ImageResult.Failure) imageResult).getErrorString();
                Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show();
            }
            return null;
        };


        new AlertDialog.Builder(requireContext())
                .setItems(new String[]{"Gallery", "Camera"}, (dialog, which) -> {
                    if (which==0){
                        mImagePicker.pickFromStorage(imageCallback);
                    }
                    else {
                        mImagePicker.takeFromCamera(imageCallback);
                    }
                }).setTitle("Image Source")
                .create()
                .show();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.single_property, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // User is not admin and not the owner
        if ((!mSinglePropertyViewModel.isAdmin() &&
                (mProperty!=null && !mProperty.getOwnerId()
                        .equals(Integer.parseInt(mSinglePropertyViewModel.getUserId()))))) {
            menu.clear();
        }
        MenuItem itemVerify = menu.findItem(R.id.action_editDoneDelete_verify);
        if (itemVerify!=null) {
            if (!mSinglePropertyViewModel.isAdmin())
                itemVerify.setVisible(false);
            itemVerify.setTitle(mProperty.getIsVerified() ? R.string.unverify : R.string.verify);
        }
        MenuItem itemEnable = menu.findItem(R.id.action_editDoneDelete_enable);
        if (itemEnable!=null) {
            itemEnable.setTitle(mProperty.getIsEnabled() ? R.string.disable : R.string.enable);
        }
    }


    @Override
    public void onPropertyAmenitiesChanged(List<Amenity> added, List<Amenity> removed) {
        List<Amenity> currentList = mAmenityAdapter.getCurrentList();
        List<Amenity> newAmenities = new ArrayList<>(currentList);
        newAmenities.addAll(added);
        newAmenities.removeAll(removed);
        mAmenityAdapter.submitList(newAmenities);
        mFragmentViewPropertyBinding.linearLayoutViewPropertyEmptyAmenities.setVisibility(newAmenities.size()>0?View.GONE:View.VISIBLE);
    }
}