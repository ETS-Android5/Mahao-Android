package ke.co.tonyoa.mahao.app.utils;

import static ke.co.tonyoa.mahao.ui.home.HomeViewModel.DEFAULT_COORDINATES;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ke.co.tonyoa.mahao.app.api.requests.CreatePropertyRequest;
import ke.co.tonyoa.mahao.app.api.requests.CreateUserRequest;
import ke.co.tonyoa.mahao.app.api.requests.DummyPropertyRequest;

public class ImportData {

    private final Set<String> mNames;
    private final List<CreatePropertyRequest> mCreatePropertyRequests;

    public ImportData(){
        String json = "[\n" +
                "  {\n" +
                "    \"Akinyi\": \"Zawadi\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Chepkirui\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Makena\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Wawuda\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Naserian\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Wawira\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Kerubo\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Rehema\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Nekesa\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Kioko\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Mukami\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Natori\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Obuya\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Okeyo\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Olouch\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Gacoki\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Keanjaho\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Ochieng\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"Akinyi\": \"Wachiru\"\n" +
                "  }\n" +
                "]";
        mNames = new HashSet<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int index = 0; index<jsonArray.length(); index++){
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                    String name = it.next();
                    mNames.add(name);
                    mNames.add(jsonObject.getString(name));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String properties = "[\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1843-1.jpg\",\n" +
                "    \"title\": \"Bedsitter\",\n" +
                "    \"price\": \"8,000\",\n" +
                "    \"location\": \"Juja. JKUAT Gate C area\",\n" +
                "    \"short_desc\": \"Spacious bedsitter - JKUAT Gate C area\",\n" +
                "    \"phone\": \"tel:0721966456\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1880-1.jpg\",\n" +
                "    \"title\": \"Benflo Residence\",\n" +
                "    \"price\": \"8,000\",\n" +
                "    \"location\": \"JKUAT Juja Gate C area\",\n" +
                "    \"short_desc\": \"Elegant bedsitter with Wi-Fi located near JKUAT Juja (Gate C area)\",\n" +
                "    \"phone\": \"tel:+254715966869\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/08/Outer-View.jpg\",\n" +
                "    \"title\": \"2-bedroom\",\n" +
                "    \"price\": \"18,000\",\n" +
                "    \"location\": \"Gachororo, Juja near Thika Superhighway\",\n" +
                "    \"short_desc\": \"Affordable two bedroom unit to let in a chilled and secure compound meters from the Thika Superhighway\",\n" +
                "    \"phone\": \"tel:+254725789206\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1894-1.jpg\",\n" +
                "    \"title\": \"Ibis Tower\",\n" +
                "    \"price\": \"5,500\",\n" +
                "    \"location\": \"Juja, near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Affordable and nice bedsitter near JKUAT Gate C Juja\",\n" +
                "    \"phone\": \"tel:+254729992628\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1876-1.jpg\",\n" +
                "    \"title\": \"Victon House\",\n" +
                "    \"price\": \"7,000\",\n" +
                "    \"location\": \"Juja near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Nice and Lovely bedsitter near JKUAT Gate C\",\n" +
                "    \"phone\": \"tel:+254729992628\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/DSC_0194.png\",\n" +
                "    \"title\": \"Lamac Apartments\",\n" +
                "    \"price\": \"35,000\",\n" +
                "    \"location\": \"Near Juja Main Stage\",\n" +
                "    \"short_desc\": \"Large shop with Plenty of water and tight security. Located along Gatundu-Juja road near Juja Square.\",\n" +
                "    \"phone\": \"tel:+254722819924\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/DSC_0399.jpg\",\n" +
                "    \"title\": \"Hotel Blue Horns\",\n" +
                "    \"price\": \"3,000\",\n" +
                "    \"location\": \"Along Juja-Gatundu Road\",\n" +
                "    \"short_desc\": \"24-hour room accommodation service. Free Breakfast provided. TV set and WI-FI installed in the room. Free private parking available. Conference facilities available.\",\n" +
                "    \"phone\": \"tel:+254720082574\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/Outside.jpg\",\n" +
                "    \"title\": \"Beacon Plaza\",\n" +
                "    \"price\": \"12,000\",\n" +
                "    \"location\": \"JKUAT Gate B\",\n" +
                "    \"short_desc\": \"One - bedroom house with free Wi-Fi installed in the premise. Located along Gachororo Road next to First Class Hotel, Juja.\",\n" +
                "    \"phone\": \"tel:+254727143427\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_2005.jpg\",\n" +
                "    \"title\": \"Zone 81\",\n" +
                "    \"price\": \"12,000\",\n" +
                "    \"location\": \"Juja near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Classic shop space with a private bathroom and kitchen bay\",\n" +
                "    \"phone\": \"tel:+254743213776\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1998.jpg\",\n" +
                "    \"title\": \"ZONE 81\",\n" +
                "    \"price\": \"7,500\",\n" +
                "    \"location\": \"Juja near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Classic bedsitter with a posh and conducive study room.\",\n" +
                "    \"phone\": \"tel:+254743213776\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1884-1.jpg\",\n" +
                "    \"title\": \"Ibis Tower\",\n" +
                "    \"price\": \"5,500\",\n" +
                "    \"location\": \"Juja, near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Nice and affordable bedsitters near JKUAT Gate C Juja\",\n" +
                "    \"phone\": \"tel:+254729992628\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/DSC_0194.png\",\n" +
                "    \"title\": \"Lamac Apartments\",\n" +
                "    \"price\": \"35,000\",\n" +
                "    \"location\": \"Near Juja Main Stage\",\n" +
                "    \"short_desc\": \"Large shop with Plenty of water and tight security. Located along Gatundu-Juja road near Juja Square.\",\n" +
                "    \"phone\": \"tel:+254722819924\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/John-Apartments-4.jpg\",\n" +
                "    \"title\": \"Bedsitter\",\n" +
                "    \"price\": \"7,000\",\n" +
                "    \"location\": \"Near Hotel Senate Juja\",\n" +
                "    \"short_desc\": \"A quality bed sitter with a regular clean water supply. Located behind Hotel Senate, Juja along Thika Super Highway.\",\n" +
                "    \"phone\": \"tel:+254725652287\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1911.jpg\",\n" +
                "    \"title\": \"Double Room\",\n" +
                "    \"price\": \"4,000\",\n" +
                "    \"location\": \"JKUAT Gate C area\",\n" +
                "    \"short_desc\": \"Affordable double room to let just next to JKUAT Gate C\",\n" +
                "    \"phone\": \"tel:+254722356892\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/08/Outer-View.jpg\",\n" +
                "    \"title\": \"2-bedroom\",\n" +
                "    \"price\": \"18,000\",\n" +
                "    \"location\": \"Gachororo, Juja near Thika Superhighway\",\n" +
                "    \"short_desc\": \"Affordable two bedroom unit to let in a chilled and secure compound meters from the Thika Superhighway\",\n" +
                "    \"phone\": \"tel:+254725789206\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_0028.jpg\",\n" +
                "    \"title\": \"ZONE 81\",\n" +
                "    \"price\": \"10,000\",\n" +
                "    \"location\": \"Juja near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Elegant bedsitter with a swanky study room\",\n" +
                "    \"phone\": \"tel:+254743213776\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/DSC_0292.jpg\",\n" +
                "    \"title\": \"Oasis House\",\n" +
                "    \"price\": \"5,000\",\n" +
                "    \"location\": \"Near Juja Posta\",\n" +
                "    \"short_desc\": \"Furniture (Bed, Study desk) and mattress provided.  Free and regular water supply\",\n" +
                "    \"phone\": \"tel:+254710215194\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_0011.jpg\",\n" +
                "    \"title\": \"ZONE 81\",\n" +
                "    \"price\": \"9,500\",\n" +
                "    \"location\": \"Juja near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Exclusive bedsitter with a posh conducive study room\",\n" +
                "    \"phone\": \"tel:+254743213776\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1998.jpg\",\n" +
                "    \"title\": \"ZONE 81\",\n" +
                "    \"price\": \"7,500\",\n" +
                "    \"location\": \"Juja near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Classic bedsitter with a posh and conducive study room.\",\n" +
                "    \"phone\": \"tel:+254743213776\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/Ebs3.jpg\",\n" +
                "    \"title\": \"Bedsitter\",\n" +
                "    \"price\": \"5,500\",\n" +
                "    \"location\": \"Gate C\",\n" +
                "    \"short_desc\": \"Cheap bedsitter near JKUAT Gate C\",\n" +
                "    \"phone\": \"tel:+254722356892\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1875.jpg\",\n" +
                "    \"title\": \"Benflo Residence\",\n" +
                "    \"price\": \"8,000\",\n" +
                "    \"location\": \"JKUAT Gate C Area\",\n" +
                "    \"short_desc\": \"Elegant bedsitter in Juja. JKUAT Gate C Area\",\n" +
                "    \"phone\": \"tel:+254715966869\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/01/01.jpg\",\n" +
                "    \"title\": \"Nickrose House\",\n" +
                "    \"price\": \"5,500\",\n" +
                "    \"location\": \"Gate C\",\n" +
                "    \"short_desc\": \"Affordable bedsitter to let near JKUAT Gate C\",\n" +
                "    \"phone\": \"tel:+254721598845\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_2005.jpg\",\n" +
                "    \"title\": \"Zone 81\",\n" +
                "    \"price\": \"12,000\",\n" +
                "    \"location\": \"Juja near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Classic shop space with a private bathroom and kitchen bay\",\n" +
                "    \"phone\": \"tel:+254743213776\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1843-1.jpg\",\n" +
                "    \"title\": \"Bedsitter\",\n" +
                "    \"price\": \"8,000\",\n" +
                "    \"location\": \"Juja. JKUAT Gate C area\",\n" +
                "    \"short_desc\": \"Spacious bedsitter - JKUAT Gate C area\",\n" +
                "    \"phone\": \"tel:0721966456\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_0011.jpg\",\n" +
                "    \"title\": \"ZONE 81\",\n" +
                "    \"price\": \"9,500\",\n" +
                "    \"location\": \"Juja near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Exclusive bedsitter with a posh conducive study room\",\n" +
                "    \"phone\": \"tel:+254743213776\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1854.jpg\",\n" +
                "    \"title\": \"Benflo Residence\",\n" +
                "    \"price\": \"8,000\",\n" +
                "    \"location\": \"Gate C\",\n" +
                "    \"short_desc\": \"Elegant bedsitter- JKUAT Gate C Area\",\n" +
                "    \"phone\": \"tel:+254715966869\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/DSC_0406-e1605434227250.jpg\",\n" +
                "    \"title\": \"Blue Horns Hotel\",\n" +
                "    \"price\": \"4,500\",\n" +
                "    \"location\": \"Along Juja-Gatundu Road\",\n" +
                "    \"short_desc\": \"24-hour room accommodation service. Two separate beds. Free Breakfast provided. TV set and WI-FI installed in the room. Free private parking available. Conference facilities available.\",\n" +
                "    \"phone\": \"tel:+254752760711\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/01/B01.jpg\",\n" +
                "    \"title\": \"Nickrose House\",\n" +
                "    \"price\": \"5,500\",\n" +
                "    \"location\": \"JKUAT Juja Gate C\",\n" +
                "    \"short_desc\": \"Affordable bedsitter to let near JKUAT Gate C\",\n" +
                "    \"phone\": \"tel:+254721598845\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/08/Toilet.jpg\",\n" +
                "    \"title\": \"Two-bedroom\",\n" +
                "    \"price\": \"18,000\",\n" +
                "    \"location\": \"Gachororo, Juja near Thika Superhighway\",\n" +
                "    \"short_desc\": \"Two bedroom units with spacious rooms and compound off Thika-Nairobi Highway\",\n" +
                "    \"phone\": \"tel:+254725789206\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1894-1.jpg\",\n" +
                "    \"title\": \"Ibis Tower\",\n" +
                "    \"price\": \"5,500\",\n" +
                "    \"location\": \"Juja, near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Affordable and nice bedsitter near JKUAT Gate C Juja\",\n" +
                "    \"phone\": \"tel:+254729992628\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1910.jpg\",\n" +
                "    \"title\": \"Ibis Tower\",\n" +
                "    \"price\": \"5,500\",\n" +
                "    \"location\": \"Juja, near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Nice and affordable bedsitter near JKUAT Gate C Juja\",\n" +
                "    \"phone\": \"tel:+254729992628\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/Ebs3.jpg\",\n" +
                "    \"title\": \"Bedsitter\",\n" +
                "    \"price\": \"5,500\",\n" +
                "    \"location\": \"Gate C\",\n" +
                "    \"short_desc\": \"Cheap bedsitter near JKUAT Gate C\",\n" +
                "    \"phone\": \"tel:+254722356892\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1854.jpg\",\n" +
                "    \"title\": \"Benflo Residence\",\n" +
                "    \"price\": \"8,000\",\n" +
                "    \"location\": \"Gate C\",\n" +
                "    \"short_desc\": \"Elegant bedsitter- JKUAT Gate C Area\",\n" +
                "    \"phone\": \"tel:+254715966869\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/Hotel-Lillies-3-1.jpg\",\n" +
                "    \"title\": \"Hotel Lillies\",\n" +
                "    \"price\": \"2,500\",\n" +
                "    \"location\": \"Sunrise Street, Juja\",\n" +
                "    \"short_desc\": \"Affordable deluxe class hotel lodging with a private bathroom. Free common room lobby with a television set. The hotel has an operational restaurant, a club house and an outdoor swimming pool.\",\n" +
                "    \"phone\": \"tel:+25494888237\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/House-of-Grace-4.jpg\",\n" +
                "    \"title\": \"House of Grace\",\n" +
                "    \"price\": \"6,700\",\n" +
                "    \"location\": \"Juja-Gatundu Road, Gate C junction\",\n" +
                "    \"short_desc\": \"An immaculate and affordable bedsitter. Security is top notch. Free and plenty of water available throughout.\",\n" +
                "    \"phone\": \"tel:+254726583912\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/DSC_0404-1.jpg\",\n" +
                "    \"title\": \"Blue Horns Hotel\",\n" +
                "    \"price\": \"2,000\",\n" +
                "    \"location\": \"Along Juja-Gatundu Road\",\n" +
                "    \"short_desc\": \"24-hour room accommodation service. Free Breakfast provided. TV set and WI-FI installed in the room. Free private parking available. Conference facilities available.\",\n" +
                "    \"phone\": \"tel:+254720082574\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_0011.jpg\",\n" +
                "    \"title\": \"ZONE 81\",\n" +
                "    \"price\": \"9,500\",\n" +
                "    \"location\": \"Juja near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Exclusive bedsitter with a posh conducive study room\",\n" +
                "    \"phone\": \"tel:+254743213776\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/John-Apartments-2-1.jpg\",\n" +
                "    \"title\": \"John Apartment\",\n" +
                "    \"price\": \"7,000\",\n" +
                "    \"location\": \"Near Hotel Senate Juja\",\n" +
                "    \"short_desc\": \"A quality bed sitter with a regular clean water supply. Located behind Hotel Senate, Juja along Thika Super Highway.\",\n" +
                "    \"phone\": \"tel:+254725652287\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1876.jpg\",\n" +
                "    \"title\": \"Benflo Residence\",\n" +
                "    \"price\": \"8,000\",\n" +
                "    \"location\": \"Juja,near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Luxurious bedsitter - JKUAT Gate C Area\",\n" +
                "    \"phone\": \"tel:+254715966869\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/02/FVBs3.jpg\",\n" +
                "    \"title\": \"Fountain Villa\",\n" +
                "    \"price\": \"8,000\",\n" +
                "    \"location\": \"JKUAT Juja Gate C area\",\n" +
                "    \"short_desc\": \"New, deluxe class bedsitter near JKUAT Juja Gate C\",\n" +
                "    \"phone\": \"tel:+254723220331\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/Ebs3.jpg\",\n" +
                "    \"title\": \"Bedsitter\",\n" +
                "    \"price\": \"5,500\",\n" +
                "    \"location\": \"Gate C\",\n" +
                "    \"short_desc\": \"Cheap bedsitter near JKUAT Gate C\",\n" +
                "    \"phone\": \"tel:+254722356892\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1847-2.jpg\",\n" +
                "    \"title\": \"Victon House\",\n" +
                "    \"price\": \"7,000\",\n" +
                "    \"location\": \"Juja near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Nice and Lovely bedsitter near JKUAT Gate C\",\n" +
                "    \"phone\": \"tel:+254729992628\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/Outside.jpg\",\n" +
                "    \"title\": \"Beacon Plaza\",\n" +
                "    \"price\": \"12,000\",\n" +
                "    \"location\": \"JKUAT Gate B\",\n" +
                "    \"short_desc\": \"One - bedroom house with free Wi-Fi installed in the premise. Located along Gachororo Road next to First Class Hotel, Juja.\",\n" +
                "    \"phone\": \"tel:+254727143427\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1894-1.jpg\",\n" +
                "    \"title\": \"Ibis Tower\",\n" +
                "    \"price\": \"5,500\",\n" +
                "    \"location\": \"Juja, near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Affordable and nice bedsitter near JKUAT Gate C Juja\",\n" +
                "    \"phone\": \"tel:+254729992628\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/urithi-5.jpg\",\n" +
                "    \"title\": \"Urithi Bed Sitter\",\n" +
                "    \"price\": \"7,000\",\n" +
                "    \"location\": \"Along Juja Farm road\",\n" +
                "    \"short_desc\": \"Tight Security, Regular water supply.\",\n" +
                "    \"phone\": \"tel:+25499000546\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/John-Apartments-2-1.jpg\",\n" +
                "    \"title\": \"John Apartment\",\n" +
                "    \"price\": \"7,000\",\n" +
                "    \"location\": \"Near Hotel Senate Juja\",\n" +
                "    \"short_desc\": \"A quality bed sitter with a regular clean water supply. Located behind Hotel Senate, Juja along Thika Super Highway.\",\n" +
                "    \"phone\": \"tel:+254725652287\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/DSC_0399.jpg\",\n" +
                "    \"title\": \"Hotel Blue Horns\",\n" +
                "    \"price\": \"3,000\",\n" +
                "    \"location\": \"Along Juja-Gatundu Road\",\n" +
                "    \"short_desc\": \"24-hour room accommodation service. Free Breakfast provided. TV set and WI-FI installed in the room. Free private parking available. Conference facilities available.\",\n" +
                "    \"phone\": \"tel:+254720082574\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/12/Nyota2.jpg\",\n" +
                "    \"title\": \"Nyota\",\n" +
                "    \"price\": \"6,000\",\n" +
                "    \"location\": \"Opposite Juja City Mall\",\n" +
                "    \"short_desc\": \"A spacious and affordable bedsitter located along Thika-Nairobi Highway opposite Juja City Mall.\",\n" +
                "    \"phone\": \"tel:+254727514300\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/11/Hotel-Lillies-3-1.jpg\",\n" +
                "    \"title\": \"Hotel Lillies\",\n" +
                "    \"price\": \"2,500\",\n" +
                "    \"location\": \"Sunrise Street, Juja\",\n" +
                "    \"short_desc\": \"Affordable deluxe class hotel lodging with a private bathroom. Free common room lobby with a television set. The hotel has an operational restaurant, a club house and an outdoor swimming pool.\",\n" +
                "    \"phone\": \"tel:+25494888237\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1858-1.jpg\",\n" +
                "    \"title\": \"Victon House\",\n" +
                "    \"price\": \"7,000\",\n" +
                "    \"location\": \"Juja near JKUAT Gate C\",\n" +
                "    \"short_desc\": \"Nice and Lovely bedsitter near JKUAT Gate C\",\n" +
                "    \"phone\": \"tel:+254729992628\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/08/Toilet.jpg\",\n" +
                "    \"title\": \"Two-bedroom\",\n" +
                "    \"price\": \"18,000\",\n" +
                "    \"location\": \"Gachororo, Juja near Thika Superhighway\",\n" +
                "    \"short_desc\": \"Two bedroom units with spacious rooms and compound off Thika-Nairobi Highway\",\n" +
                "    \"phone\": \"tel:+254725789206\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2020/12/Nyota2.jpg\",\n" +
                "    \"title\": \"Nyota\",\n" +
                "    \"price\": \"7,500\",\n" +
                "    \"location\": \"Vacant\",\n" +
                "    \"short_desc\": \"A spacious and affordable bedsitter located along Thika-Nairobi Highway opposite Juja City Mall.\",\n" +
                "    \"phone\": \"tel:+254727514300\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/02/FVBs4-05.jpg\",\n" +
                "    \"title\": \"Fountain Villa\",\n" +
                "    \"price\": \"10,000\",\n" +
                "    \"location\": \"Opposite Juja City Mall\",\n" +
                "    \"short_desc\": \"A quality bedsitter with uninterrupted water supply and tight security.\",\n" +
                "    \"phone\": \"tel:+254723220331\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1843-1.jpg\",\n" +
                "    \"title\": \"Bedsitter\",\n" +
                "    \"price\": \"6,000\",\n" +
                "    \"location\": \"Vacant\",\n" +
                "    \"short_desc\": \"Spacious bedsitter - JKUAT Gate C area\",\n" +
                "    \"phone\": \"tel:0721966456\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/Eh.jpg\",\n" +
                "    \"title\": \"Single Room\",\n" +
                "    \"price\": \"5,000\",\n" +
                "    \"location\": \"JKUAT Juja Gate C area\",\n" +
                "    \"short_desc\": \"Cheap single room near JKUAT\",\n" +
                "    \"phone\": \"tel:+254722356892\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"feature\": \"https://kampasways.com/wp-content/uploads/2021/07/DSC_1876.jpg\",\n" +
                "    \"title\": \"Benflo Residence\",\n" +
                "    \"price\": \"7,000\",\n" +
                "    \"location\": \"Vacant\",\n" +
                "    \"short_desc\": \"Luxurious bedsitter - JKUAT Gate C Area\",\n" +
                "    \"phone\": \"tel:+254715966869\"\n" +
                "  }\n" +
                "]";

        Gson gson = new Gson();
        List<DummyPropertyRequest> dummyPropertyRequests = gson.fromJson(properties, new TypeToken<List<DummyPropertyRequest>>() {}.getType());
        mCreatePropertyRequests = new ArrayList<>();
        for (DummyPropertyRequest dummyPropertyRequest:dummyPropertyRequests){
            float price = 0;
            try {
                price = Float.parseFloat(dummyPropertyRequest.getPrice().replace(" ", "").replace(",", ""));
            }
            catch (NumberFormatException e){
                Log.e("Invalid price", dummyPropertyRequest.getPrice()+" "+e.getMessage());
            }
            CreatePropertyRequest createPropertyRequest = new CreatePropertyRequest(6,
                    dummyPropertyRequest.getTitle(), dummyPropertyRequest.getShortDesc(), 0, 0,
                    dummyPropertyRequest.getLocation(),
                    price,
                    Arrays.asList(DEFAULT_COORDINATES.latitude+"", DEFAULT_COORDINATES.longitude+""),
                    true, true, dummyPropertyRequest.getFeature());
            mCreatePropertyRequests.add(createPropertyRequest);
        }
    }



    public List<CreateUserRequest> getCreateUsers(){
        List<CreateUserRequest> createUserRequests = new ArrayList<>();
        for (int num=0; num<20; num++) {
            String firstName = getRandomName();
            String lastName = getRandomName();
            String email = firstName + lastName + "@gmail.com";
            String phone = "07";
            for (int index = 0; index < 8; index++) {
                Random random = new Random();
                phone = phone + (random.nextInt(10));
            }
            CreateUserRequest createUserRequest = new CreateUserRequest(firstName, lastName, email,
                    phone, "Juja", false, true, false, "password");
            createUserRequests.add(createUserRequest);
        }
        return createUserRequests;
    }

    public List<CreatePropertyRequest> getCreatePropertyRequests() {
        return mCreatePropertyRequests;
    }

    private String getRandomName(){
        List<String> names = new ArrayList<>(mNames);
        Random random = new Random();
        return names.get(random.nextInt(names.size()));
    }


}
