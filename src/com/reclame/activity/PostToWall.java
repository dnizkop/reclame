package com.reclame.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.reclame.R;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.api.model.VKWallPostResult;

public class PostToWall extends Activity {
	
	static String user_id = null;
	static String name = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_wall);

		Intent intent =  getIntent();
		name = intent.getStringExtra("name");
		
		VKApi.users().get().executeWithListener(new VKRequest.VKRequestListener() {
		    @Override
		    public void onComplete(VKResponse response) {
		        VKApiUser user = ((VKList<VKApiUser>)response.parsedModel).get(0);
		        Log.d("User name", user.first_name + " " + user.last_name);
		        
		        user_id = String.valueOf(user.id);		   		        		        		        
		        
		        
		        Toast.makeText(getBaseContext(), user_id + " " + name, Toast.LENGTH_LONG).show();
		        makePost(null, name);
		    }
		});

		

	}

	@Override
	protected void onResume() {
		super.onResume();
		VKUIHelper.onResume(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VKUIHelper.onDestroy(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		VKUIHelper.onActivityResult(requestCode, resultCode, data);
	}

	private void makePost(VKAttachments attachments) {
		makePost(attachments, null);
	}

	private void makePost(VKAttachments attachments, String message) {

		VKRequest post = VKApi.wall().post(
				VKParameters.from(VKApiConst.OWNER_ID, user_id,
						VKApiConst.ATTACHMENTS, attachments,
						VKApiConst.MESSAGE, message));
		post.setModelClass(VKWallPostResult.class);

		post.executeWithListener(new VKRequestListener() {
			@Override
			public void onComplete(VKResponse response) {
				super.onComplete(response);
				
				Intent i = new Intent(getBaseContext(), WellDone.class);
				
				
				/*Intent i = new Intent(
						Intent.ACTION_VIEW,
						Uri.parse(String
								.format("https://vk.com/"+user_id+"_%s",
										((VKWallPostResult) response.parsedModel).post_id)));*/
				startActivity(i);
			}

			@Override
			public void onError(VKError error) {
				 //showError(error.apiError != null ? error.apiError : error);
			}
		});		
	}
}
