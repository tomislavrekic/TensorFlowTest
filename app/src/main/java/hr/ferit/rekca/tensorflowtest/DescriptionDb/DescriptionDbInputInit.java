package hr.ferit.rekca.tensorflowtest.DescriptionDb;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class DescriptionDbInputInit {


    private static List<String> initLabels(Context context) {
        List<String> labels = new ArrayList<>();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("labels.txt")));
            String line;
            while((line = reader.readLine()) != null){
                labels.add(line);
            }
            return labels;
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private static List<String> initDesc(Context context){
        return initLabels(context);
    }

    //TODO: method for reading descriptions

    public static void initDb(Context context){
        DescriptionDbController tempController = new DescriptionDbController(context);

        if(tempController.readAll().size() != 0){
            return;
        }

        List<String> labels = initLabels(context);
        List<String> descs = initDesc(context);

        for(int i=0; i<labels.size();i++){
            DescriptionDbSingleUnit tempUnit = new DescriptionDbSingleUnit(labels.get(i), descs.get(i), "", 0.0f, 0, "01/01/1000" );
            //TODO: make an "empty" picture for init

            tempController.insertRow(tempUnit);
        }


    }



}
