package ke.co.tonyoa.mahao.app.api;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import ke.co.tonyoa.mahao.app.api.requests.CreateFeedbackRequest;
import ke.co.tonyoa.mahao.app.api.requests.CreatePropertyRequest;
import ke.co.tonyoa.mahao.app.api.requests.CreateUserRequest;
import ke.co.tonyoa.mahao.app.api.requests.ModifyAmenitiesRequest;
import ke.co.tonyoa.mahao.app.api.requests.RemovePropertyPhotoRequest;
import ke.co.tonyoa.mahao.app.api.requests.ResetPasswordRequest;
import ke.co.tonyoa.mahao.app.api.requests.UpdateUserRequest;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.api.responses.FavoriteResponse;
import ke.co.tonyoa.mahao.app.api.responses.Feedback;
import ke.co.tonyoa.mahao.app.api.responses.LoginResponse;
import ke.co.tonyoa.mahao.app.api.responses.ModifyAmenitiesResponse;
import ke.co.tonyoa.mahao.app.api.responses.PasswordRecoveryResponse;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.api.responses.PropertyPhoto;
import ke.co.tonyoa.mahao.app.api.responses.ResetPasswordResponse;
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.enums.FeedbackType;
import ke.co.tonyoa.mahao.app.enums.SortBy;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;
import ke.co.tonyoa.mahao.app.utils.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class ApiManager {
    private static final int READ_TIMEOUT = 60;
    private static final int CONNECT_TIMEOUT = 60;
    private static final int WRITE_TIMEOUT = 60;
    private static final String AUTH_PREFIX = "Bearer ";
    private static final int NUMBER_OF_THREADS = 6;
    private static final ExecutorService apiExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public static final String API_URL = "https://5ca6-185-199-103-169.ngrok.io";
    private final RestApi api;
    private final Application application;
    private final SharedPrefs mSharedPrefs;

    @Inject
    public ApiManager(Application application, SharedPrefs sharedPrefs) {
        this.application = application;
        this.mSharedPrefs = sharedPrefs;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(API_URL)
                .client(client)
                .build();

        api = retrofit.create(RestApi.class);
    }


    public static void execute(Runnable runnable) {
        apiExecutor.execute(runnable);
    }

    @Nullable
    private RequestBody getPart(String value) {
        return value == null ? null : RequestBody.create(value, MediaType.parse("text/plain"));
    }

    @Nullable
    private MultipartBody.Part getFilePart(Uri uri, String name) {
        if (uri == null) {
            return null;
        }
        if (name == null) {
            name = "file";
        }
        byte[] fileData = FileUtils.readFile(application, uri);
        if (fileData == null) {
            return null;
        }
        DocumentFile attachment = FileUtils.fromUri(application, uri);
        String mimeType = FileUtils.getType(application, uri);
        RequestBody fileRequestBody = RequestBody.create(fileData, MediaType.parse(mimeType));
        return MultipartBody.Part.createFormData(name, attachment.getName(), fileRequestBody);
    }

    private String getToken() {
        return AUTH_PREFIX + mSharedPrefs.getToken();
    }

    public APIResponse<LoginResponse> login(String username, String password) throws IOException {
        return new APIResponse<>(api.login(getPart(username), getPart(password)).execute());
    }

    public APIResponse<PasswordRecoveryResponse> recoverPassword(String email) throws IOException {
        return new APIResponse<>(api.recoverPassword(email).execute());
    }

    public APIResponse<ResetPasswordResponse> resetPassword(String token, String newPassword) throws IOException {
        return new APIResponse<>(api.resetPassword(new ResetPasswordRequest(token, newPassword)).execute());
    }

    public APIResponse<List<User>> getUsers(int skip, int limit) throws IOException {
        return new APIResponse<>(api.getUsers(getToken(), skip, limit).execute());
    }

    public APIResponse<User> getUserProfile() throws IOException {
        return new APIResponse<>(api.getUserProfile(getToken()).execute());
    }

    public APIResponse<User> createUser(String firstName, String lastName, String email, String phone,
                                        String location, boolean isVerified, boolean isActive,
                                        boolean isSuperUser, String password) throws IOException {
        return new APIResponse<>(api.createUser(getToken(),
                new CreateUserRequest(firstName, lastName, email, phone, location, isVerified,
                        isActive, isSuperUser, password)).execute());
    }

    public APIResponse<User> updateUser(String firstName, String lastName, String phone,
                                        String location, String password) throws IOException {
        return new APIResponse<>(api.updateUser(getToken(),
                new UpdateUserRequest(password, firstName, lastName, phone, location)).execute());
    }

    public APIResponse<User> register(String firstName, String lastName, String email, String phone,
                                        String location, boolean isVerified, boolean isActive,
                                        boolean isSuperUser, String password) throws IOException {
        return new APIResponse<>(api.register(new CreateUserRequest(firstName, lastName, email, phone,
                location, isVerified, isActive, isSuperUser, password)).execute());
    }


    public APIResponse<User> getUserById(int userId) throws IOException {
        return new APIResponse<>(api.getUserById(getToken(), userId).execute());
    }

    public APIResponse<List<Property>> getProperties(int skip, int limit, SortBy sortBy, LatLng latLngSort,
                                                     String query, Integer minBed, Integer maxBed, Integer minBath,
                                                     Integer maxBath, Float minPrice, Float maxPrice,
                                                     LatLng latLngFilter, Integer filterRadius, Boolean isVerified,
                                                     Boolean isEnabled, List<Integer> categories, List<Integer> amenities) throws IOException {
        if (categories!=null && categories.size()==0)
            categories = null;
        if (amenities!=null && amenities.size()==0)
            amenities = null;
        if (sortBy!=SortBy.DISTANCE && sortBy!=SortBy.NEG_DISTANCE){
            latLngSort = null;
        }
        return new APIResponse<>(api.getProperties(getToken(), skip, limit, sortBy==null?null:sortBy.getApiValue(),
                latLngSort==null?null:latLngSort.latitude+"", latLngSort==null?null:latLngSort.longitude+"",
                query, minBed, maxBed, minBath, maxBath, minPrice, maxPrice,
                (latLngFilter!=null && filterRadius!=null)? (latLngFilter.latitude+", "+latLngFilter.longitude+", "+filterRadius):null,
                isVerified, isEnabled, categories==null?null:categories.toString().replace("[", "").replace("]", ""),
                amenities==null?null:amenities.toString().replace("[", "").replace("]", "")).execute());
    }

    public APIResponse<List<Property>> getUserProperties(int skip, int limit) throws IOException {
        return new APIResponse<>(api.getMyProperties(getToken(), skip, limit).execute());
    }

    public APIResponse<List<Property>> getFavoriteProperties(int skip, int limit) throws IOException {
        return new APIResponse<>(api.getFavoriteProperties(getToken(), skip, limit).execute());
    }

    public APIResponse<List<Property>> getLatestProperties(Integer category, int skip, int limit) throws IOException {
        return new APIResponse<>(api.getLatestProperties(getToken(), category, skip, limit).execute());
    }

    public APIResponse<List<Property>> getPopularProperties(Integer category, int skip, int limit) throws IOException {
        return new APIResponse<>(api.getPopularProperties(getToken(), category, skip, limit).execute());
    }

    public APIResponse<List<Property>> getRecommendedProperties(Integer category, int skip, int limit) throws IOException {
        return new APIResponse<>(api.getRecommendedProperties(getToken(), category, skip, limit).execute());
    }

    public APIResponse<List<Property>> getSimilarProperties(int propertyId, Integer category, int skip, int limit) throws IOException {
        return new APIResponse<>(api.getSimilarProperties(getToken(), propertyId, category, skip, limit).execute());
    }

    public APIResponse<Property> getPropertyById(int propertyId) throws IOException {
        return new APIResponse<>(api.getPropertyById(getToken(), propertyId).execute());
    }

    public APIResponse<Property> createProperty(Uri featureImage, int propertyCategoryId, String title,
                                            String description, int numBed, int numBath, String locationName,
                                            float price, LatLng latLng, boolean isEnabled, boolean isVerified) throws IOException {

        return new APIResponse<>(api.createProperty(getToken(), getFilePart(featureImage, "feature_image"),
                getPart(propertyCategoryId+""), getPart(title), getPart(description), getPart(numBed+""),
                getPart(numBath+""), getPart(locationName), getPart(price+""), getPart(latLng.latitude+""),
                getPart(latLng.longitude+""), getPart(isEnabled+""), getPart(isVerified+"")).execute());
    }

    public APIResponse<Property> createProperty(CreatePropertyRequest createPropertyRequest) throws IOException {
        return new APIResponse<>(api.createProperty(getToken(), createPropertyRequest).execute());
    }

    public APIResponse<Property> updateProperty(int propertyId, Uri featureImage, int propertyCategoryId, String title,
                                                String description, int numBed, int numBath, String locationName,
                                                float price, LatLng latLng, boolean isEnabled, boolean isVerified) throws IOException {

        return new APIResponse<>(api.updateProperty(getToken(), propertyId, getFilePart(featureImage, "feature_image"),
                getPart(propertyCategoryId+""), getPart(title), getPart(description), getPart(numBed+""),
                getPart(numBath+""), getPart(locationName), getPart(price+""), getPart(latLng.latitude+""),
                getPart(latLng.longitude+""), getPart(isEnabled+""), getPart(isVerified+"")).execute());
    }

    public APIResponse<Property> deletePropertyById(int propertyId) throws IOException {
        return new APIResponse<>(api.deletePropertyById(getToken(), propertyId).execute());
    }

    public APIResponse<List<ModifyAmenitiesResponse>> modifyPropertyAmenities(int propertyId, List<Integer> added,
                                                                        List<Integer> removed) throws IOException {
        return new APIResponse<>(api.modifyPropertyAmenities(getToken(), propertyId,
                new ModifyAmenitiesRequest(added, removed)).execute());
    }

    public APIResponse<FavoriteResponse> addFavorite(int propertyId) throws IOException {
        return new APIResponse<>(api.addFavorite(getToken(), propertyId).execute());
    }

    public APIResponse<FavoriteResponse> removeFavorite(int propertyId) throws IOException {
        return new APIResponse<>(api.removeFavorite(getToken(), propertyId).execute());
    }

    public APIResponse<Feedback> addFeedback(int propertyId, FeedbackType feedbackType) throws IOException {
        return addFeedback(getToken(), propertyId, feedbackType);
    }

    public APIResponse<Feedback> addFeedback(String token, int propertyId, FeedbackType feedbackType) throws IOException {
        return new APIResponse<>(api.addFeedback(token, propertyId, new CreateFeedbackRequest(feedbackType.name())).execute());
    }

    public APIResponse<List<PropertyPhoto>> addPropertyPhotos(int propertyId, List<Uri> photos) throws IOException {
        List<MultipartBody.Part> parts = new ArrayList<>();
        if (photos != null){
            for (Uri uri : photos){
                parts.add(getFilePart(uri, "photos"));
            }
        }
        return new APIResponse<>(api.addPropertyPhotos(getToken(), propertyId, parts).execute());
    }

    public APIResponse<PropertyPhoto> removePropertyPhoto(int propertyId, int propertyPhotoId) throws IOException {
        return new APIResponse<>(api.removePropertyPhoto(getToken(), propertyId, new RemovePropertyPhotoRequest(propertyPhotoId)).execute());
    }

    public APIResponse<List<Amenity>> getAmenities(int skip, int limit) throws IOException {
        return new APIResponse<>(api.getAmenities(getToken(), skip, limit).execute());
    }

    public APIResponse<Amenity> createAmenity(Uri icon, String title, String description) throws IOException {
        return new APIResponse<>(api.createAmenity(getToken(), getFilePart(icon, "icon"), getPart(title),
                getPart(description)).execute());
    }

    public APIResponse<Amenity> updateAmenity(int amenityId, Uri icon, String title, String description) throws IOException {
        return new APIResponse<>(api.updateAmenity(getToken(), amenityId,  getFilePart(icon, "icon"), getPart(title),
                getPart(description)).execute());
    }

    public APIResponse<Amenity> getAmenityById(int amenityId) throws IOException {
        return new APIResponse<>(api.getAmenityById(getToken(), amenityId).execute());
    }

    public APIResponse<Amenity> deleteAmenityById(int amenityId) throws IOException {
        return new APIResponse<>(api.deleteAmenityById(getToken(), amenityId).execute());
    }

    public APIResponse<List<PropertyCategory>> getPropertyCategories(int skip, int limit) throws IOException {
        return new APIResponse<>(api.getPropertyCategories(getToken(), skip, limit).execute());
    }

    public APIResponse<PropertyCategory> createPropertyCategory(Uri icon, String title, String description) throws IOException {
        return new APIResponse<>(api.createPropertyCategory(getToken(), getFilePart(icon, "icon"), getPart(title),
                getPart(description)).execute());
    }

    public APIResponse<PropertyCategory> updatePropertyCategory(int propertyCategoryId, Uri icon, String title, String description) throws IOException {
        return new APIResponse<>(api.updatePropertyCategory(getToken(), propertyCategoryId,  getFilePart(icon, "icon"), getPart(title),
                getPart(description)).execute());
    }

    public APIResponse<PropertyCategory> getPropertyCategoryById(int amenityId) throws IOException {
        return new APIResponse<>(api.getPropertyCategoryById(getToken(), amenityId).execute());
    }

    public APIResponse<PropertyCategory> deletePropertyCategoryById(int amenityId) throws IOException {
        return new APIResponse<>(api.deletePropertyCategoryById(getToken(), amenityId).execute());
    }

}