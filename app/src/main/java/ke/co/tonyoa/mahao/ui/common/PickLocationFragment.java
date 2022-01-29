package ke.co.tonyoa.mahao.ui.common;

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
import androidx.core.content.ContextCompat;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentPickLocationBinding;

public class PickLocationFragment extends BaseFragment implements OnMapReadyCallback{

    private FragmentPickLocationBinding mFragmentPickLocationBinding;
    private GoogleMap mGoogleMap;
    private PlacesClient mPlacesClient;
    private ActivityResultLauncher<String> mPermissionResultLauncher;
    private ActivityResultLauncher<Intent> mLocationEnableLauncher;
    private String mLocationName;
    private float[] mCoordinates;
    private boolean mClicked = true;

    public PickLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mLocationName = PickLocationFragmentArgs.fromBundle(getArguments()).getName();
            mCoordinates = PickLocationFragmentArgs.fromBundle(getArguments()).getCoordinates();
        }
        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result!=null && result){
                setupCurrentLocation();
            }
            else {
                Snackbar.make(mFragmentPickLocationBinding.linearLayoutLocationPickerFooter,
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
        mFragmentPickLocationBinding = FragmentPickLocationBinding.inflate(inflater, container, false);
        mFragmentPickLocationBinding.layoutToolbar.materialToolbarLayoutToolbar.setTitle("Pick Location");
        mFragmentPickLocationBinding.layoutToolbar.materialToolbarLayoutToolbar.setNavigationOnClickListener(v -> {
            navigateBack();
        });

        // Initialize the Places SDK
        Places.initialize(requireContext(), getString(R.string.google_maps_key));

        // Create a new PlacesClient instance
        mPlacesClient = Places.createClient(requireContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.locationPicker_maps);
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
                    mClicked = true;
                    if (place.getLatLng() != null)
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12));
                    mFragmentPickLocationBinding.textViewLocationPickerCurrentAddress.setText(place.getName()+", "+place.getAddress());
                }


                @Override
                public void onError(@NonNull Status status) {
                    Log.e("PICK_LOCATION", "An error occurred: " + status);
                }
            });
        }

        mFragmentPickLocationBinding.locationPickerDestinationButton.setOnClickListener(v->{
            LatLng selectedLocation = mGoogleMap.getCameraPosition().target;
            String selectedAddress = ViewUtils.getText(mFragmentPickLocationBinding.textViewLocationPickerCurrentAddress);

            LocationWithLatLng locationWithLatLng = new LocationWithLatLng(selectedAddress, selectedLocation);
            NavBackStackEntry previousBackStackEntry = getNavController().getPreviousBackStackEntry();
            if (previousBackStackEntry != null)
                previousBackStackEntry.getSavedStateHandle().set("location", locationWithLatLng);
            navigateBack();
        });

        return mFragmentPickLocationBinding.getRoot();
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
                                mFragmentPickLocationBinding.textViewLocationPickerCurrentAddress.setText(address);
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
    }

    private void setupCurrentLocation(){
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!ViewUtils.isLocationEnabled(requireContext())){
                Snackbar.make(mFragmentPickLocationBinding.linearLayoutLocationPickerFooter,
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
                            if (place.getLatLng()!=null) {
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12));
                            }
                            mFragmentPickLocationBinding.textViewLocationPickerCurrentAddress.setText(place.getName() + ", " + place.getAddress());
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
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class LocationWithLatLng implements Serializable {
        private String mLocation;
        private LatLng mLatLng;

        LocationWithLatLng(String location, LatLng latLng) {
            mLocation = location;
            mLatLng = latLng;
        }

        public String getLocation() {
            return mLocation;
        }

        public void setLocation(String location) {
            mLocation = location;
        }

        public LatLng getLatLng() {
            return mLatLng;
        }

        public void setLatLng(LatLng latLng) {
            mLatLng = latLng;
        }
    }
}