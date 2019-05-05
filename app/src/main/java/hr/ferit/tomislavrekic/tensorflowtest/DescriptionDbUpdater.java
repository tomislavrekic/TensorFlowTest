package hr.ferit.tomislavrekic.tensorflowtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import hr.ferit.tomislavrekic.tensorflowtest.DescriptionDb.DescriptionDbSingleUnit;
import hr.ferit.tomislavrekic.tensorflowtest.DescriptionDb.DescriptionDbUpdateManager;

public class DescriptionDbUpdater {
    private Context mContext;
    private List<String> mLabels;

    public DescriptionDbUpdater(Context context, List<String> labels){
        mContext=context;
        mLabels=labels;
    }

    public void updateDb(int guessedLabelIndex, float guessedActivation, Bitmap inputImage) {
        DescriptionDbUpdateManager manager = new DescriptionDbUpdateManager(mContext);

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c);

        DescriptionDbSingleUnit tempUnit = new DescriptionDbSingleUnit(mLabels.get(guessedLabelIndex), null, createImageFromBitmapDB(inputImage), guessedActivation, 0, formattedDate);

        manager.UpdateRow(tempUnit);
    }

    public String createImageFromBitmapDB(Bitmap bitmap) {
        String fileName = "tempNameFile";//no .png or .jpg needed
        try {
            Bitmap.createScaledBitmap(bitmap, 100, 100, true);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

}
