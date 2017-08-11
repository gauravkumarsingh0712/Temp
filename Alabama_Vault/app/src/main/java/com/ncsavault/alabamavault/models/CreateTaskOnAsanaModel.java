package com.ncsavault.alabamavault.models;

import android.os.AsyncTask;

import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;

import org.json.JSONObject;

/**
 * Created by gauravkumar.singh on 12/23/2016.
 */

public class CreateTaskOnAsanaModel extends BaseModel {

    private boolean statusResult;
    private String taskId;


    public void loadAsanaData(String nameAndEmail,String taskMesg,String tagId)
    {
        AsanaTask asanaTask =  new AsanaTask();
        asanaTask.execute(nameAndEmail,taskMesg,tagId);
    }

    private class AsanaTask extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... params) {
            String task_id = "";
            boolean status = true;
            try {
                String result = AppController.getInstance().getServiceManager().getVaultService().createTaskOnAsana(params[0], params[1], params[2]);

                if (result != null) {
                    if (!result.isEmpty()) {
                        JSONObject jsonResult = new JSONObject(result);
                        if(jsonResult != null){
                            JSONObject jsonData = (JSONObject) jsonResult.get("data");
                            if(jsonData != null){
                                if(jsonData.getString("id") != null) {
                                    task_id = jsonData.getString("id").toString();
                                    //Create tag for task type
                                    String tagResult;
                                    if(!params[2].isEmpty()) {
                                        tagResult = AppController.getInstance().getServiceManager().getVaultService()
                                                .createTagForAsanaTask(params[2], task_id);
                                        if (tagResult.contains("\"data\":"))
                                            status = true;
                                        else
                                            status = false;
                                    }

                                    //create tag for Platform Name
                                    tagResult = AppController.getInstance().getServiceManager().getVaultService().
                                            createTagForAsanaTask(GlobalConstants.ANDROID_TAG_ID, task_id);
                                    if(tagResult.contains("\"data\":"))
                                        status = true;
                                    else
                                        status = false;
                                }
                            }
                        }
                    }
                }
                statusResult = status;
                taskId = task_id;
                state = STATE_SUCCESS;
                informViews();
            }catch(Exception e){
                e.printStackTrace();
                status = false;
            }

            return null;
        }
    }

    public Boolean getStatusResult()
    {
        return statusResult;
    }

    public String getTaskId()
    {
        return taskId;
    }
}
