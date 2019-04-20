package hr.ferit.rekca.tensorflowtest;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import hr.ferit.rekca.tensorflowtest.DescriptionDb.DescriptionDbSingleUnit;

public class DBDialogFragment extends DialogFragment {

    Context mContext;

    public static  DBDialogFragment newInstance(DescriptionDbSingleUnit data, Context context){
        DBDialogFragment f = new DBDialogFragment();



        Bundle args = new Bundle();
        args.putSerializable(Constants.DF_NAME_KEY, data.getName());
        args.putSerializable(Constants.DF_IMAGE_KEY, data.getPicture());
        args.putSerializable(Constants.DF_GUESS_KEY, data.getGuess());
        args.putSerializable(Constants.DF_COUNT_KEY, data.getGuessCount());
        args.putSerializable(Constants.DF_INFO_KEY, data.getInfo());
        args.putSerializable(Constants.DF_DATE_KEY, data.getLastSeen());

        Log.d("DBD", "newInstance: " + data.getName() + data.getInfo() + data.getLastSeen() + data.getPicture()+String.valueOf(data.getGuess())+ String.valueOf(data.getGuessCount()));


        f.setArguments(args);

        return f;
    }

    private static void initViews() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("DBD", "onCreate: fragCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_db_fragment, container, false);

        Bundle args = getArguments();

        ImageView ivImage;
        TextView tvName;
        TextView tvDate;
        TextView tvGuess;
        TextView tvCount;
        TextView tvInfo;

        ivImage = v.findViewById(R.id.ivDetailImage);
        tvName = v.findViewById(R.id.tvDetailName);
        tvDate = v.findViewById(R.id.tvDetailDate);
        tvGuess = v.findViewById(R.id.tvDetailGuess);
        tvCount = v.findViewById(R.id.tvDetailCount);
        tvInfo = v.findViewById(R.id.tvDetailInfo);

        String tempName = args.getString(Constants.DF_NAME_KEY);
        String tempDate = args.getString(Constants.DF_DATE_KEY);
        String tempInfo = args.getString(Constants.DF_INFO_KEY);
        String tempImage = args.getString(Constants.DF_IMAGE_KEY);
        float tempGuess = args.getFloat(Constants.DF_GUESS_KEY);
        int tempCount = args.getInt(Constants.DF_COUNT_KEY);



        Log.d("DBD", "onCreateView: " + tempName + tempInfo + tempImage + tempDate+tempCount+tempGuess);



        InputStream tempStream = null;

        try {
            tempStream = v.getContext().openFileInput(tempImage);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        if(tempStream != null){
            Log.d("DBD", "onCreateView: ds");
        }

        ivImage.setImageBitmap(BitmapFactory.decodeStream(tempStream));
        tvName.setText(tempName);
        tvInfo.setText(tempInfo);
        tvDate.setText(tempDate);
        tvGuess.setText(String.valueOf(tempGuess));
        tvCount.setText(String.valueOf(tempCount));



        return v;
    }
}
