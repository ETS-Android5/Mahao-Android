package ke.co.tonyoa.mahao.ui.properties.single;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import static ke.co.tonyoa.mahao.ui.home.HomeViewModel.DEFAULT_COORDINATES;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentPropertyMapBinding;
import ke.co.tonyoa.mahao.databinding.ItemVerticalPropertyBinding;
import ke.co.tonyoa.mahao.ui.properties.PropertyAdapter;

public class PropertyMapFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private FragmentPropertyMapBinding mFragmentPropertyMapBinding;
    private PropertyMapViewModel mPropertyMapViewModel;
    private GoogleMap mGoogleMap;
    private ActivityResultLauncher<String> mPermissionResultLauncher;
    private ActivityResultLauncher<Intent> mLocationEnableLauncher;
    private PlacesClient mPlacesClient;
    private float[] mCoordinates;
    private boolean mClicked = true;
    private List<Marker> mMarkers;
    private PropertyAdapter mPropertyAdapter;
    private GoogleMap.InfoWindowAdapter mInfoWindowAdapter = new GoogleMap.InfoWindowAdapter() {
        @Nullable
        @Override
        public View getInfoContents(@NonNull Marker marker) {
            return null;
        }

        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            ItemVerticalPropertyBinding itemVerticalPropertyBinding = ItemVerticalPropertyBinding.inflate(LayoutInflater.from(requireContext()),
                    null, false);
            Property property = (Property) marker.getTag();
            Glide.with(requireContext())
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
            return itemVerticalPropertyBinding.getRoot();
        }
    };

    public PropertyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCoordinates = PropertyMapFragmentArgs.fromBundle(getArguments()).getCoordinates();
        }
        mPropertyMapViewModel = new ViewModelProvider(this).get(PropertyMapViewModel.class);
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


        // Initialize the Places SDK
        Places.initialize(requireContext(), getString(R.string.google_maps_key));

        // Create a new PlacesClient instance
        mPlacesClient = Places.createClient(requireContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment!=null) {
            mapFragment.getMapAsync(this);
        }

        return mFragmentPropertyMapBinding.getRoot();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng latlng = new LatLng(mCoordinates==null?DEFAULT_COORDINATES.latitude:mCoordinates[0],
                mCoordinates==null?DEFAULT_COORDINATES.longitude:mCoordinates[1]);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));
        setupCurrentLocation();

        mGoogleMap.setOnCameraIdleListener(() -> {
            LatLng latLng = mGoogleMap.getCameraPosition().target;
            // Only get place data if not set from search or current position
            if (!mClicked) {
                new Thread(() -> {
                    try {
                        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                        final List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        requireActivity().runOnUiThread(()->{
                            if (!addresses.isEmpty()) {
                                String address = addresses.get(0).getAddressLine(0);
                                //mFragmentPickLocationBinding.textViewLocationPickerCurrentAddress.setText(address);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            else {
                mClicked = false;
            }
        });

        fetchProperties();
    }

    private void fetchProperties() {
        mPropertyAdapter = new PropertyAdapter(PropertyAdapter.ListType.VERTICAL_PROPERTY,
                mFragmentPropertyMapBinding.layoutBottomsheetMap.recyclerViewBottomSheetMapProperties.getWidth(), requireContext(),
                (property, position) -> {
                    navigate(PropertyMapFragmentDirections.actionPropertyMapFragmentToSinglePropertyFragment(property));
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


        mPropertyMapViewModel.getProperties(mCoordinates[0], mCoordinates[1], mCoordinates[2]).observe(getViewLifecycleOwner(), listAPIResponse -> {
            if (listAPIResponse!=null && listAPIResponse.isSuccessful()){
                if (mMarkers!=null){
                    for (Marker marker:mMarkers){
                        marker.remove();
                    }
                }
                List<Property> properties = listAPIResponse.body();
                mMarkers = new ArrayList<>();
                for (Property property:properties){
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .title(property.getTitle())
                            .snippet(property.getLocationName())
                            .icon(BitmapDescriptorFactory.fromBitmap(ViewUtils.drawableToBitmap(ResourcesCompat.getDrawable(getResources(),
                                    R.drawable.ic_home_black_24dp, null))))
                            .position(new LatLng(property.getLocation().get(0), property.getLocation().get(1))));
                    Log.e("Add property", "Add property for "+property.getTitle()+" with coordinates "+property.getLocation());
                    if (marker!=null) {
                        marker.setTag(property);
                        mMarkers.add(marker);
                    }
                }
                mFragmentPropertyMapBinding.layoutBottomsheetMap.textViewBottomSheetMapTitle.setText("Nearby Properties: "+properties.size());
                mFragmentPropertyMapBinding.layoutBottomsheetMap.textViewBottomSheetMapContent.setText("Swipe Up");
                mPropertyAdapter.submitList(properties);


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
                            mClicked = true;
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12));

                            //mFragmentPickLocationBinding.textViewLocationPickerCurrentAddress.setText(place.getName() + ", " + place.getAddress());
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
        navigate(PropertyMapFragmentDirections.actionPropertyMapFragmentToSinglePropertyFragment(property));
    }
}