package ke.co.tonyoa.mahao.app.api;


import java.util.List;

import ke.co.tonyoa.mahao.app.api.requests.CreateFeedbackRequest;
import ke.co.tonyoa.mahao.app.api.requests.CreateUserRequest;
import ke.co.tonyoa.mahao.app.api.requests.ModifyAmenitiesRequest;
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
import ke.co.tonyoa.mahao.app.api.requests.RemovePropertyPhotoRequest;
import ke.co.tonyoa.mahao.app.api.responses.ResetPasswordResponse;
import ke.co.tonyoa.mahao.app.api.responses.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApi {

    /**
     * OAuth2 compatible token login, get an access token for future requests
     * @param username
     * @param password
     * @return
     */
    @Multipart
    @POST("/api/v1/login/access-token")
    @Headers({"Accept: application/json"})
    Call<LoginResponse> login(@Part("username") RequestBody username,
                              @Part("password") RequestBody password);

    /**
     * Password recovery
     * @param email
     * @return
     */
    @POST("/api/v1/password-recovery/{email}")
    @Headers({"Accept: application/json"})
    Call<PasswordRecoveryResponse> recoverPassword(@Path("email") String email);


    /**
     * Reset password
     * @param resetPasswordRequest
     * @return
     */
    @POST("/api/v1/reset-password/")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ResetPasswordResponse> resetPassword(@Body() ResetPasswordRequest resetPasswordRequest);

    /**
     * Retrieve users.
     * @param token
     * @param skip
     * @param limit
     * @return
     */
    @GET("/api/v1/users/")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<List<User>> getUsers(@Header("Authorization") String token, @Query("skip") Integer skip,
                              @Query("limit") Integer limit);

    /**
     * Get current user profile
     * @param token
     * @return
     */
    @GET("/api/v1/users/me")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<User> getUserProfile(@Header("Authorization") String token);

    /**
     * Create new user as an admin
     * @param token
     * @param createUserRequest
     * @return
     */
    @POST("/api/v1/users/")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<User> createUser(@Header("Authorization") String token, @Body CreateUserRequest createUserRequest);

    /**
     * Update own user profile
     * @param token
     * @param updateUserRequest
     * @return
     */
    @PUT("/api/v1/users/me")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<User> updateUser(@Header("Authorization") String token, @Body UpdateUserRequest updateUserRequest);

    /**
     * Create new user without the need to be logged in
     * @param createUserRequest
     * @return
     */
    @POST("/api/v1/users/open")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<User> register(@Body CreateUserRequest createUserRequest);

    /**
     * Get a specific user by id.
     * @param token
     * @param userId
     * @return
     */
    @GET("/api/v1/users/{id}")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<User> getUserById(@Header("Authorization") String token, @Path("id") int userId);

    /**
     * Retrieve properties.
     * @param token
     * @param skip
     * @param limit
     * @param sortBy the sort options as either -time, time, -price, price, -distance, distance
     * @param sortLatitude The latitude to use when sorting by distance
     * @param sortLongitude The longitude to use when sorting by distance
     * @param query The search query
     * @param minBed Minimum number of bedrooms
     * @param maxBed Maximum number of bedrooms
     * @param minBath Minimum number of bathrooms
     * @param maxBath Maximum number of bathrooms
     * @param minPrice Minimum price of a property
     * @param maxPrice Maximum price of a property
     * @param coordinates Coordinates to filter with for nearby houses in the
     *                    form lat, long, radius in kilometres e.g. 41.5, 20.4, 2
     * @param isVerified Whether to filter only houses that are verified or not
     * @param isEnabled Whether tp filter only houses that are enabled or not
     * @param categories Category IDs to use when filtering houses separated by commas e.g 4, 7, 12
     * @param amenities Amenity IDs to use when filtering houses separated by commas e.g 2, 6, 8
     * @return
     */
    @GET("/api/v1/properties/")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<List<Property>> getProperties(@Header("Authorization") String token,
                                       @Query("skip") Integer skip,
                                       @Query("limit") Integer limit,
                                       @Query("sort") String sortBy,
                                       @Query("sort_latitude") Double sortLatitude,
                                       @Query("sort_longitude") Double sortLongitude,
                                       @Query("q") String query,
                                       @Query("min_bed") Integer minBed,
                                       @Query("max_bed") Integer maxBed,
                                       @Query("min_bath") Integer minBath,
                                       @Query("max_bath") Integer maxBath,
                                       @Query("min_price") Float minPrice,
                                       @Query("max_price") Float maxPrice,
                                       @Query("location") String coordinates,
                                       @Query("is_verified") Boolean isVerified,
                                       @Query("is_enabled") Boolean isEnabled,
                                       @Query("categories") String categories,
                                       @Query("amenities") String amenities);

    /**
     * Retrieve properties by logged in user.
     * @param token
     * @param skip
     * @param limit
     * @return
     */
    @GET("/api/v1/properties/me/")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<List<Property>> getMyProperties(@Header("Authorization") String token,
                                       @Query("skip") Integer skip,
                                       @Query("limit") Integer limit);

    /**
     * Retrieve favorite properties by logged in user.
     * @param token
     * @param skip
     * @param limit
     * @return
     */
    @GET("/api/v1/properties/favorites/")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<List<Property>> getFavoriteProperties(@Header("Authorization") String token,
                                         @Query("skip") Integer skip,
                                         @Query("limit") Integer limit);

    /**
     * Retrieve the latest properties
     * @param token
     * @param category
     * @param skip
     * @param limit
     * @return
     */
    @GET("/api/v1/properties/latest")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<List<Property>> getLatestProperties(@Header("Authorization") String token,
                                               @Query("category") Integer category,
                                               @Query("skip") Integer skip,
                                               @Query("limit") Integer limit);

    /**
     * Retrieve the popular properties
     * @param token
     * @param category
     * @param skip
     * @param limit
     * @return
     */
    @GET("/api/v1/properties/popular")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<List<Property>> getPopularProperties(@Header("Authorization") String token,
                                             @Query("category") Integer category,
                                             @Query("skip") Integer skip,
                                             @Query("limit") Integer limit);

    /**
     * Retrieve properties recommended for the logged in user
     * @param token
     * @param category
     * @param skip
     * @param limit
     * @return
     */
    @GET("/api/v1/properties/recommended")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<List<Property>> getRecommendedProperties(@Header("Authorization") String token,
                                              @Query("category") Integer category,
                                              @Query("skip") Integer skip,
                                              @Query("limit") Integer limit);

    /**
     * Retrieve properties similar to a property
     * @param token
     * @param propertyId
     * @param category
     * @param skip
     * @param limit
     * @return
     */
    @GET("/api/v1/properties/{id}/similar")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<List<Property>> getSimilarProperties(@Header("Authorization") String token,
                                                  @Path("id") int propertyId,
                                                  @Query("category") Integer category,
                                                  @Query("skip") Integer skip,
                                                  @Query("limit") Integer limit);

    /**
     * Get property by ID.
     * @param token
     * @param propertyId
     * @return
     */
    @GET("/api/v1/properties/{id}")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<Property> getPropertyById(@Header("Authorization") String token, @Path("id") int propertyId);

    /**
     * Create new property.
     * @param token
     * @param featureImage
     * @param propertyCategoryId
     * @param title
     * @param description
     * @param numBedrooms
     * @param numBathrooms
     * @param locationName
     * @param price
     * @param latitude
     * @param longitude
     * @param isEnabled
     * @param isVerified
     * @return
     */
    @Multipart
    @POST("/api/v1/properties/")
    @Headers({"Accept: application/json"})
    Call<Property> createProperty(
            @Header("Authorization") String token,
            @Part MultipartBody.Part featureImage,
            @Part("property_category_id") RequestBody propertyCategoryId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("num_bed") RequestBody numBedrooms,
            @Part("num_bath") RequestBody numBathrooms,
            @Part("location_name") RequestBody locationName,
            @Part("price") RequestBody price,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("is_enabled") RequestBody isEnabled,
            @Part("is_verified") RequestBody isVerified);

    /**
     * Update a property.
     * @param token
     * @param propertyId
     * @param featureImage
     * @param propertyCategoryId
     * @param title
     * @param description
     * @param numBedrooms
     * @param numBathrooms
     * @param locationName
     * @param price
     * @param latitude
     * @param longitude
     * @param isEnabled
     * @param isVerified
     * @return
     */
    @Multipart
    @PUT("/api/v1/properties/{id}")
    @Headers({"Accept: application/json"})
    Call<Property> updateProperty(
            @Header("Authorization") String token,
            @Path("id") int propertyId,
            @Part MultipartBody.Part featureImage,
            @Part("property_category_id") RequestBody propertyCategoryId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("num_bed") RequestBody numBedrooms,
            @Part("num_bath") RequestBody numBathrooms,
            @Part("location_name") RequestBody locationName,
            @Part("price") RequestBody price,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("is_enabled") RequestBody isEnabled,
            @Part("is_verified") RequestBody isVerified);

    /**
     * Delete a property.
     * @param token
     * @param propertyId
     * @return
     */
    @DELETE("/api/v1/properties/{id}")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<Property> deletePropertyById(@Header("Authorization") String token, @Path("id") int propertyId);

    /**
     * Modify property amenities to property by the property ID, with those being added and removed.
     * @param token
     * @param propertyId
     * @param modifyAmenitiesRequest
     * @return
     */
    @POST("/api/v1/properties/{id}/modify_property_amenities")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<List<ModifyAmenitiesResponse>> modifyPropertyAmenities(@Header("Authorization") String token, @Path("id") int propertyId,
                                           @Body ModifyAmenitiesRequest modifyAmenitiesRequest);

    /**
     * Make the property a favorite for the user
     * @param token
     * @param propertyId
     * @return
     */
    @POST("/api/v1/properties/{id}/add_favorite")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<FavoriteResponse> addFavorite(@Header("Authorization") String token, @Path("id") int propertyId);

    /**
     * Remove the property from the favorite by the user
     * @param token
     * @param propertyId
     * @return
     */
    @POST("/api/v1/properties/{id}/remove_favorite")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<FavoriteResponse> removeFavorite(@Header("Authorization") String token, @Path("id") int propertyId);

    /**
     * Add feedback to the property by logged in user
     * @param token
     * @param propertyId
     * @param createFeedbackRequest
     * @return
     */
    @POST("/api/v1/properties/{id}/add_feedback")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<Feedback> addFeedback(@Header("Authorization") String token, @Path("id") int propertyId,
                                       @Body CreateFeedbackRequest createFeedbackRequest);

    /**
     * Add photos to the property
     * @param token
     * @param propertyId
     * @param photos
     * @return
     */
    @Multipart
    @POST("/api/v1/properties/{id}/add_property_photos")
    @Headers({"Accept: application/json"})
    Call<List<PropertyPhoto>> addPropertyPhotos(
            @Header("Authorization") String token,
            @Path("id") int propertyId,
            @Part List<MultipartBody.Part> photos);

    /**
     * Remove photo from the property
     * @param token
     * @param propertyId
     * @param removePropertyPhotoRequest
     * @return
     */
    @POST("/api/v1/properties/{id}/remove_property_photos")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<PropertyPhoto> removePropertyPhoto(
            @Header("Authorization") String token,
            @Path("id") int propertyId,
            @Body RemovePropertyPhotoRequest removePropertyPhotoRequest);


    /**
     * Retrieve amenities.
     * @param token
     * @param skip
     * @param limit
     * @return
     */
    @GET("/api/v1/amenities/")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<List<Amenity>> getAmenities(@Header("Authorization") String token,
                                      @Query("skip") Integer skip,
                                      @Query("limit") Integer limit);

    /**
     * Create new amenity.
     * @param token
     * @param icon
     * @param title
     * @param description
     * @return
     */
    @Multipart
    @POST("/api/v1/amenities/")
    @Headers({"Accept: application/json"})
    Call<Amenity> createAmenity(
            @Header("Authorization") String token,
            @Part MultipartBody.Part icon,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description);

    /**
     * Get amenity by ID.
     * @param token
     * @param amenityId
     * @return
     */
    @GET("/api/v1/amenities/{id}")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<Amenity> getAmenityById(@Header("Authorization") String token, @Path("id") int amenityId);

    /**
     * Update an amenity.
     * @param token
     * @param amenityId
     * @param icon
     * @param title
     * @param description
     * @return
     */
    @Multipart
    @PUT("/api/v1/amenities/{id}")
    @Headers({"Accept: application/json"})
    Call<Amenity> updateAmenity(
            @Header("Authorization") String token,
            @Path("id") int amenityId,
            @Part MultipartBody.Part icon,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description);

    /**
     * Delete an amenity.
     * @param token
     * @param amenityId
     * @return
     */
    @DELETE("/api/v1/amenities/{id}")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<Amenity> deleteAmenityById(@Header("Authorization") String token, @Path("id") int amenityId);

    /**
     * Retrieve property categories.
     * @param token
     * @param skip
     * @param limit
     * @return
     */
    @GET("/api/v1/property_categories/")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<List<PropertyCategory>> getPropertyCategories(@Header("Authorization") String token,
                                                       @Query("skip") Integer skip,
                                                       @Query("limit") Integer limit);

    /**
     * Create new property category.
     * @param token
     * @param icon
     * @param title
     * @param description
     * @return
     */
    @Multipart
    @POST("/api/v1/property_categories/")
    @Headers({"Accept: application/json"})
    Call<PropertyCategory> createPropertyCategory(
            @Header("Authorization") String token,
            @Part MultipartBody.Part icon,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description);

    /**
     * Get property category by ID.
     * @param token
     * @param propertyCategoryId
     * @return
     */
    @GET("/api/v1/property_categories/{id}")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<PropertyCategory> getPropertyCategoryById(@Header("Authorization") String token, @Path("id") int propertyCategoryId);

    /**
     * Update a property category.
     * @param token
     * @param propertyCategoryId
     * @param icon
     * @param title
     * @param description
     * @return
     */
    @Multipart
    @PUT("/api/v1/property_categories/{id}")
    @Headers({"Accept: application/json"})
    Call<PropertyCategory> updatePropertyCategory(
            @Header("Authorization") String token,
            @Path("id") int propertyCategoryId,
            @Part MultipartBody.Part icon,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description);

    /**
     * Delete a property category.
     * @param token
     * @param propertyCategoryId
     * @return
     */
    @DELETE("/api/v1/property_categories/{id}")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<PropertyCategory> deletePropertyCategoryById(@Header("Authorization") String token,
                                                      @Path("id") int propertyCategoryId);
}