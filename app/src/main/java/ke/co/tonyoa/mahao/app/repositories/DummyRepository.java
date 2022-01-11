package ke.co.tonyoa.mahao.app.repositories;

import android.app.Application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.ApiManager;
import ke.co.tonyoa.mahao.app.api.requests.CreatePropertyRequest;
import ke.co.tonyoa.mahao.app.api.requests.CreateUserRequest;
import ke.co.tonyoa.mahao.app.api.responses.LoginResponse;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.enums.FeedbackType;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;
import ke.co.tonyoa.mahao.app.utils.ImportData;

@Singleton
public class DummyRepository {

    ApiManager mApiManager;
    SharedPrefs mSharedPrefs;
    private Application mApplication;
    private ImportData mImportData = new ImportData();

    @Inject
    public DummyRepository(Application application, ApiManager apiManager, SharedPrefs sharedPrefs){
        mApplication = application;
        mApiManager = apiManager;
        mSharedPrefs = sharedPrefs;
    }

    public void createDummyData(){
        createUsers();
        createProperties();
    }

    public void createUsers(){
        List<CreateUserRequest> createUsers = mImportData.getCreateUsers();
        for (CreateUserRequest createUserRequest:createUsers){
            ApiManager.execute(()->{
                try {
                    APIResponse<User> userAPIResponse = mApiManager.createUser(createUserRequest.getFirstName(), createUserRequest.getLastName(),
                            createUserRequest.getEmail(), createUserRequest.getPhone(), createUserRequest.getLocation(),
                            createUserRequest.getIsVerified(), createUserRequest.getIsActive(), createUserRequest.getIsSuperuser(),
                            createUserRequest.getPassword());
                    if (userAPIResponse!=null && userAPIResponse.isSuccessful()){
                        User user = userAPIResponse.body();
                        APIResponse<LoginResponse> loginAPIResponse = mApiManager.login(createUserRequest.getEmail(), createUserRequest.getPassword());
                        if (loginAPIResponse!=null && loginAPIResponse.isSuccessful()){
                            String token = loginAPIResponse.body().getToken().getAccessToken();
                            String idEmailToken = user.getId() +
                                    "\t" +
                                    user.getId() +
                                    "\t" +
                                    user.getEmail() +
                                    "\t" +
                                    token;
                            mSharedPrefs.addToList("emails", idEmailToken);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void createProperties(){
        List<CreatePropertyRequest> createProperties = mImportData.getCreatePropertyRequests();
        ApiManager.execute(()->{
            try {
                for (CreatePropertyRequest createPropertyRequest : createProperties) {
                    APIResponse<Property> propertyAPIResponse = mApiManager.createProperty(createPropertyRequest);
                    if (propertyAPIResponse != null && propertyAPIResponse.isSuccessful()) {
                        mSharedPrefs.addToList("properties", propertyAPIResponse.body().getId() + "");
                    }
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    public void createFeedbacks(){
        List<String> users = mSharedPrefs.getList("emails");
        List<String> properties = mSharedPrefs.getList("properties");
        int propertiesSize = properties.size();
        ApiManager.execute(()->{
            for (String user:users){
                try {
                    String token = user.split("\t")[3];

                    //View up to 20 random properties
                    Random randomViewSize = new Random(System.currentTimeMillis());
                    List<String> viewedProperties = new ArrayList<>();
                    int numViews = randomViewSize.nextInt(20);
                    for (int index=0; index<numViews;){
                        Random randomProperty = new Random(System.currentTimeMillis());
                        String property = properties.get(randomProperty.nextInt(propertiesSize));
                        if (!viewedProperties.contains(property)){
                            viewedProperties.add(property);
                            numViews++;
                            mApiManager.addFeedback(token, Integer.parseInt(property), FeedbackType.VIEW);
                        }
                    }

                    // Click up to 10 random properties
                    Random randomClickSize = new Random(System.currentTimeMillis());
                    List<String> clickedProperties = new ArrayList<>();
                    int numClicks = randomClickSize.nextInt(numViews);
                    for (int index=0; index<numClicks;){
                        Random randomProperty = new Random(System.currentTimeMillis());
                        String property = viewedProperties.get(randomProperty.nextInt(numViews));
                        if (!clickedProperties.contains(property)){
                            clickedProperties.add(property);
                            numClicks++;
                            mApiManager.addFeedback(token, Integer.parseInt(property), FeedbackType.CLICK);
                        }
                    }

                    //Favorite up to 5 random properties
                    Random randomFavoriteSize = new Random(System.currentTimeMillis());
                    List<String> favoriteProperties = new ArrayList<>();
                    int numFavorites = randomFavoriteSize.nextInt(3);
                    for (int index=0; index<numFavorites;){
                        Random randomProperty = new Random(System.currentTimeMillis());
                        String property = clickedProperties.get(randomProperty.nextInt(numFavorites));
                        if (!favoriteProperties.contains(property)){
                            favoriteProperties.add(property);
                            numFavorites++;
                            mApiManager.addFeedback(token, Integer.parseInt(property), FeedbackType.FAVORITE);
                        }
                    }

                    //Call up to 3 random properties
                    Random randomCallSize = new Random(System.currentTimeMillis());
                    List<String> callProperties = new ArrayList<>();
                    int numCalls = randomCallSize.nextInt(3);
                    for (int index=0; index<numCalls;){
                        Random randomProperty = new Random(System.currentTimeMillis());
                        String property = clickedProperties.get(randomProperty.nextInt(numCalls));
                        if (!callProperties.contains(property)){
                            callProperties.add(property);
                            numCalls++;
                            mApiManager.addFeedback(token, Integer.parseInt(property), FeedbackType.CALL);
                        }
                    }

                    //Text up to 3 random properties
                    Random randomTextSize = new Random(System.currentTimeMillis());
                    List<String> textProperties = new ArrayList<>();
                    int numTexts = randomTextSize.nextInt(numClicks);
                    for (int index=0; index<numTexts;){
                        Random randomProperty = new Random(System.currentTimeMillis());
                        String property = clickedProperties.get(randomProperty.nextInt(numTexts));
                        if (!textProperties.contains(property)){
                            textProperties.add(property);
                            numTexts++;
                            mApiManager.addFeedback(token, Integer.parseInt(property), FeedbackType.TEXT);
                        }
                    }

                    //Map up to 3 random properties
                    Random randomMapSize = new Random(System.currentTimeMillis());
                    List<String> mapProperties = new ArrayList<>();
                    int numMaps = randomMapSize.nextInt(3);
                    for (int index=0; index<numMaps;){
                        Random randomProperty = new Random(System.currentTimeMillis());
                        String property = clickedProperties.get(randomProperty.nextInt(numMaps));
                        if (!mapProperties.contains(property)){
                            mapProperties.add(property);
                            numMaps++;
                            mApiManager.addFeedback(token, Integer.parseInt(property), FeedbackType.MAP);
                        }
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
