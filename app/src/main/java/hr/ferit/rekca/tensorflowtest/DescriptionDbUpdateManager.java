package hr.ferit.rekca.tensorflowtest;

import android.content.Context;

public class DescriptionDbUpdateManager {
    DescriptionDbController controller;
    private static float minGuessValue = 0.5f;

    public DescriptionDbUpdateManager(Context context){
        controller = new DescriptionDbController(context);
    }

    public void UpdateRow(DescriptionDbSingleUnit input){
        if(input.getGuess() < minGuessValue){
            return;
        }

        DescriptionDbSingleUnit tempUnit = controller.readDb(input.getName()).get(0);
        input.setGuessCount(1 + tempUnit.getGuessCount());

        //TODO: For possible full update, check if Info is different, do that inside DescriptionDbSingleUnit class

        if(input.getGuess() > tempUnit.getGuess()){
            UpdatePic(input);
        }
        else {
            UpdateGuess(input);
        }
    }

    private void UpdateFull(DescriptionDbSingleUnit input){
        controller.updateRow(input, DescriptionDbController.Mode.UPDATE_FULL);
    }

    private void UpdatePic(DescriptionDbSingleUnit input){
        controller.updateRow(input, DescriptionDbController.Mode.UPDATE_PICTURE);
    }

    private void UpdateGuess(DescriptionDbSingleUnit input){
        controller.updateRow(input, DescriptionDbController.Mode.UPDATE_COUNTER);
    }
}
