package ke.co.tonyoa.mahao.ui.common;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentPropertyMapBinding;
import ke.co.tonyoa.mahao.databinding.ItemVerticalPropertyBinding;
import ke.co.tonyoa.mahao.ui.main.MainFragmentDirections;
import ke.co.tonyoa.mahao.ui.properties.PropertyAdapter;

public class PropertyMapFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private FragmentPropertyMapBinding mFragmentPropertyMapBinding;
    private PropertyMapViewModel mPropertyMapViewModel;
    private GoogleMap mGoogleMap;
    private ActivityResultLauncher<String> mPermissionResultLauncher;
    private ActivityResultLauncher<Intent> mLocationEnableLauncher;
    private PlacesClient mPlacesClient;
    private float[] mCoordinates;
    private int mPropertyId = -1;
    private PropertyAdapter mPropertyAdapter;
    private final GoogleMap.InfoWindowAdapter mInfoWindowAdapter = new GoogleMap.InfoWindowAdapter() {
        @Nullable
        @Override
        public View getInfoContents(@NonNull Marker marker) {
            ItemVerticalPropertyBinding itemVerticalPropertyBinding = ItemVerticalPropertyBinding.inflate(LayoutInflater.from(requireContext()),
                    null, false);
            Property property = (Property) marker.getTag();
            if (property == null)
                return null;
            Glide.with(requireContext())
                    .asBitmap()
                    .override(500, 500)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            if (e != null)
                                e.printStackTrace();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            if (marker.isInfoWindowShown()) {
                                marker.hideInfoWindow();
                                marker.showInfoWindow();
                            }
                            return false;
                        }
                    })
                    .load(property.getFeatureImage())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(itemVerticalPropertyBinding.imageViewItemVerticalPropertyFeature);

            itemVerticalPropertyBinding.textViewItemVerticalPropertyLocation.setText(property.getLocationName());
            itemVerticalPropertyBinding.animationViewItemVerticalPropertyLike.setProgress(property.getIsFavorite() ? 1 : 0);
            itemVerticalPropertyBinding.imageViewItemVerticalPropertyVerified.setVisibility(property.getIsVerified() ?
                    View.VISIBLE : View.GONE);
            itemVerticalPropertyBinding.textViewItemVerticalPropertyTitle.setText(property.getTitle());
            itemVerticalPropertyBinding.textViewItemVerticalPropertyPrice.setText(requireContext().getString(R.string.kes_f_month, property.getPrice()));
            itemVerticalPropertyBinding.textViewItemVerticalPropertyBeds.setText(requireContext().getString(R.string.d_beds, property.getNumBed()));
            itemVerticalPropertyBinding.textViewItemVerticalPropertyBaths.setText(requireContext().getString(R.string.d_baths, property.getNumBath()));
            itemVerticalPropertyBinding.textViewItemVerticalPropertyAmenities.setText(requireContext().getString(R.string.d_amens, property.getPropertyAmenities().size()));
            itemVerticalPropertyBinding.linearLayoutItemVerticalPropertyLikeContainer.setVisibility(View.GONE);
            return itemVerticalPropertyBinding.getRoot();
        }

        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            return null;
        }
    };

    public PropertyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            PropertyMapFragmentArgs propertyMapFragmentArgs = PropertyMapFragmentArgs.fromBundle(getArguments());
            mCoordinates = propertyMapFragmentArgs.getCoordinates();
            mPropertyId = propertyMapFragmentArgs.getPropertyId();
        }
        PropertyMapViewModelFactory propertyMapViewModelFactory = new PropertyMapViewModelFactory(requireActivity().getApplication(),
                mCoordinates==null?null:new PropertyMapViewModel.LatLngRadius(mCoordinates[0], mCoordinates[1],
                        mCoordinates[2]));
        mPropertyMapViewModel = new ViewModelProvider(this, propertyMapViewModelFactory).get(PropertyMapViewModel.class);
        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result!=null && result){
                setupCurrentLocation();
            }
            else {
                Snackbar.make(mFragmentPropertyMapBinding.coordinatorLayoutBottomsheet,
                        "Grant Location Permission for correct functionality",
                        BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Grant", v -> {
                    mPermissionResultLauncher.launch(ACCESS_FINE_LOCATION);
                }).show();
            }
        });
        mLocationEnableLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            setupCurrentLocation();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentPropertyMapBinding = FragmentPropertyMapBinding.inflate(inflater, container, false);
        setToolbar(mFragmentPropertyMapBinding.layoutToolbar.materialToolbarLayoutToolbar);
        setTitle("View Properties");
        if (mPropertyId < 0) {
            mFragmentPropertyMapBinding.layoutToolbar.getRoot().setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams layoutParamsToolbar = (ViewGroup.MarginLayoutParams)mFragmentPropertyMapBinding.map.getLayoutParams();
            layoutParamsToolbar.setMargins(0, 0, 0, 0);
            ViewGroup.MarginLayoutParams searchLayoutParams = (ViewGroup.MarginLayoutParams)mFragmentPropertyMapBinding.materialCardViewSearch.getLayoutParams();
            int size8 = getResources().getDimensionPixelSize(R.dimen.size_8);
            searchLayoutParams.setMargins(size8, size8, size8, 0);
        }


        // Initialize the Places SDK
        Places.initialize(requireContext(), getString(R.string.google_maps_key));

        // Create a new PlacesClient instance
        mPlacesClient = Places.createClient(requireContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment!=null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            // Specify the types of place data to return.
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                    Place.Field.LAT_LNG, Place.Field.ADDRESS));
            autocompleteFragment.setLocationBias(RectangularBounds.newInstance(new LatLng( -4.893646, 33.896000 ),
                    new LatLng( 4.543865, 41.916020 ))).setCountries("KE");

            // Set up a PlaceSelectionListener to handle the response.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    if (place.getLatLng() != null)
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12));
                }


                @Override
                public void onError(@NonNull Status status) {
                    Log.e("PICK_LOCATION", "An error occurred: " + status);
                }
            });
        }

        BottomSheetBehavior.from(mFragmentPropertyMapBinding.layoutBottomsheetMap.getRoot())
                .addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        int visibility;
                        int mapMargin;
                        if (newState == STATE_EXPANDED || mPropertyId < 0) {
                            visibility = View.GONE;
                            mapMargin = 0;
                        }
                        else {
                            visibility = View.VISIBLE;
                            mapMargin = getResources().getDimensionPixelSize(R.dimen.size_56);
                        }
                        mFragmentPropertyMapBinding
                                .layoutToolbar
                                .getRoot()
                                .setVisibility(visibility);
                        mFragmentPropertyMapBinding.materialCardViewSearch.setVisibility(
                                newState==STATE_EXPANDED?View.GONE:View.VISIBLE);
                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mFragmentPropertyMapBinding.map.getLayoutParams();
                        layoutParams.setMargins(0, mapMargin, 0, 0);
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });

        mPropertyAdapter = new PropertyAdapter(PropertyAdapter.ListType.VERTICAL_PROPERTY,
                requireContext(),
                (property, position) -> {
                    if (mPropertyId >= 0) {
                        navigate(PropertyMapFragmentDirections.actionPropertyMapFragmentToSinglePropertyFragment(property));
                    }
                    else {
                        navigate(MainFragmentDirections.actionNavigationMainToSinglePropertyFragment(property));
                    }
                }, (property, position) -> {
            if (property.getIsFavorite()) {
                mPropertyMapViewModel.addFavorite(property.getId()).observe(getViewLifecycleOwner(), favoriteResponseAPIResponse -> {
                    if (favoriteResponseAPIResponse != null && favoriteResponseAPIResponse.isSuccessful()) {
                        property.setIsFavorite(true);
                    } else {
                        Toast.makeText(requireContext(),
                                (favoriteResponseAPIResponse == null || favoriteResponseAPIResponse.errorMessage(requireContext()) == null) ?
                                        getString(R.string.unknown_error) :
                                        favoriteResponseAPIResponse.errorMessage(requireContext()),
                                Toast.LENGTH_SHORT).show();
                        property.setIsFavorite(false);
                    }
                    mPropertyAdapter.notifyItemChanged(position, Arrays.asList("like"));
                });
            }
            else {
                mPropertyMapViewModel.removeFavorite(property.getId()).observe(getViewLifecycleOwner(), favoriteResponseAPIResponse -> {
                    if (favoriteResponseAPIResponse != null && favoriteResponseAPIResponse.isSuccessful()) {
                        property.setIsFavorite(false);
                    } else {
                        Toast.makeText(requireContext(),
                                (favoriteResponseAPIResponse == null || favoriteResponseAPIResponse.errorMessage(requireContext()) == null) ?
                                        getString(R.string.unknown_error) :
                                        favoriteResponseAPIResponse.errorMessage(requireContext()),
                                Toast.LENGTH_SHORT).show();
                        property.setIsFavorite(true);
                    }
                    mPropertyAdapter.notifyItemChanged(position, Arrays.asList("like"));
                });
            }
        }, null);
        mFragmentPropertyMapBinding.layoutBottomsheetMap.recyclerViewBottomSheetMapProperties.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false));
        mFragmentPropertyMapBinding.layoutBottomsheetMap.recyclerViewBottomSheetMapProperties.setAdapter(mPropertyAdapter);

        return mFragmentPropertyMapBinding.getRoot();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mPropertyMapViewModel.getLatLngRadius().observe(getViewLifecycleOwner(), latLngRadius -> {
            LatLng latlng = new LatLng(latLngRadius.getLat(),
                    latLngRadius.getLng());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,
                    mPropertyMapViewModel.getLastZoom()));
        });

        setupCurrentLocation();

        mGoogleMap.setOnCameraIdleListener(() -> {
            LatLng latLng = mGoogleMap.getCameraPosition().target;
            mPropertyMapViewModel.setLastZoom(mGoogleMap.getCameraPosition().zoom);
            mPropertyMapViewModel.setLatLngRadius((float) latLng.latitude, (float) latLng.longitude, (float) (getMapVisibleRadius()/1000));
        });

        fetchProperties();
    }

    private void fetchProperties() {
        mFragmentPropertyMapBinding.layoutBottomsheetMap.getRoot().setVisibility(View.GONE);
        mFragmentPropertyMapBinding.linearProgressIndicator.setVisibility(View.VISIBLE);
        mPropertyMapViewModel.getProperties().observe(getViewLifecycleOwner(), listAPIResponse -> {
            mFragmentPropertyMapBinding.linearProgressIndicator.setVisibility(View.GONE);
            mFragmentPropertyMapBinding.layoutBottomsheetMap.getRoot().setVisibility(View.VISIBLE);
            if (listAPIResponse!=null && listAPIResponse.isSuccessful()){
                List<Property> properties = listAPIResponse.body();
                Property selectedProperty = null;
                for (Property property:properties){
                    Drawable homeDrawable = ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_home_black_24dp, null);
                    if (Objects.equals(mPropertyId, property.getId())){
                        selectedProperty = property;
                        if (homeDrawable != null) {
                            homeDrawable.setTint(ResourcesCompat.getColor(getResources(), R.color.color_accent,
                                    null));
                        }
                    }
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .title(property.getTitle())
                            .snippet(property.getLocationName())
                            .icon(BitmapDescriptorFactory.fromBitmap(ViewUtils.drawableToBitmap(homeDrawable)))
                            .position(new LatLng(property.getLocation().get(0), property.getLocation().get(1))));
                    if (homeDrawable != null)
                        homeDrawable.setTintList(null);
                    if (marker!=null) {
                        marker.setTag(property);
                    }
                }

                String title;
                String subtitle;
                if (selectedProperty == null){
                    title = "Nearby Properties: "+properties.size();
                    subtitle = "Swipe Up";
                }
                else {
                    title = selectedProperty.getTitle();
                    subtitle = "Nearby Properties: "+properties.size();
                }
                mFragmentPropertyMapBinding.layoutBottomsheetMap.textViewBottomSheetMapTitle
                        .setText(title);
                mFragmentPropertyMapBinding.layoutBottomsheetMap.textViewBottomSheetMapContent.setText(subtitle);
                mPropertyAdapter.submitList(properties);
                mFragmentPropertyMapBinding.layoutBottomsheetMap.tapActionLayout.setOnClickListener(v->{
                    if (mGoogleMap != null && mCoordinates != null){
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCoordinates[0],
                                mCoordinates[1]), 15));
                    }
                });


                mGoogleMap.setInfoWindowAdapter(mInfoWindowAdapter);
                mGoogleMap.setOnInfoWindowClickListener(this);
            }
            else {
                Toast.makeText(requireContext(),
                        (listAPIResponse==null || listAPIResponse.errorMessage(requireContext())==null)?
                                getString(R.string.unknown_error):
                                listAPIResponse.errorMessage(requireContext()),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCurrentLocation(){
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!ViewUtils.isLocationEnabled(requireContext())){
                Snackbar.make(mFragmentPropertyMapBinding.coordinatorLayoutBottomsheet,
                        R.string.enable_your_location, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.enable, v -> {
                            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mLocationEnableLauncher.launch(intent);
                        })
                        .show();
                return;
            }

            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.setMyLocationEnabled(true);

            if (mCoordinates==null) {
                Task<FindCurrentPlaceResponse> placeResponse = mPlacesClient.findCurrentPlace(request);
                placeResponse.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FindCurrentPlaceResponse response = task.getResult();
                        List<PlaceLikelihood> placeLikelihoods = response.getPlaceLikelihoods();
                        if (placeLikelihoods.size() > 0) {
                            Place place = placeLikelihoods.get(0).getPlace();
                            if (place.getLatLng() != null){
                                mPropertyMapViewModel.setLastZoom(12);
                                LatLng latLng = place.getLatLng();
                                mPropertyMapViewModel.setLatLngRadius((float) latLng.latitude,
                                        (float) latLng.longitude, (float) getMapVisibleRadius());
                            }
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e("PICK_LOCATION", "Place not found: " + apiException.getStatusCode());
                        }
                    }
                });
            }
        }
        else {
            mPermissionResultLauncher.launch(ACCESS_FINE_LOCATION);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Property property = (Property) marker.getTag();
        if (mPropertyId >= 0) {
            navigate(PropertyMapFragmentDirections.actionPropertyMapFragmentToSinglePropertyFragment(property));
        }
        else {
            navigate(MainFragmentDirections.actionNavigationMainToSinglePropertyFragment(property));
        }
    }

    private double getMapVisibleRadius() {
        VisibleRegion visibleRegion = mGoogleMap.getProjection().getVisibleRegion();

        float[] distanceWidth = new float[1];
        float[] distanceHeight = new float[1];

        LatLng farRight = visibleRegion.farRight;
        LatLng farLeft = visibleRegion.farLeft;
        LatLng nearRight = visibleRegion.nearRight;
        LatLng nearLeft = visibleRegion.nearLeft;

        Location.distanceBetween(
                (farLeft.latitude + nearLeft.latitude) / 2,
                farLeft.longitude,
                (farRight.latitude + nearRight.latitude) / 2,
                farRight.longitude,
                distanceWidth
        );

        Location.distanceBetween(
                farRight.latitude,
                (farRight.longitude + farLeft.longitude) / 2,
                nearRight.latitude,
                (nearRight.longitude + nearLeft.longitude) / 2,
                distanceHeight
        );

        double radiusInMeters = Math.sqrt(Math.pow(distanceWidth[0], 2) + Math.pow(distanceHeight[0], 2)) / 2;
        return radiusInMeters;
    }
}