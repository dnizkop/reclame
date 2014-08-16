package com.reclame.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.reclame.R;
import com.reclame.db.DBHelper;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_wall);

		VKApi.users().get()
				.executeWithListener(new VKRequest.VKRequestListener() {
					@Override
					public void onComplete(VKResponse response) {
						VKApiUser user = ((VKList<VKApiUser>) response.parsedModel)
								.get(0);
						Log.d("User name", user.first_name + " "
								+ user.last_name);

						user_id = String.valueOf(user.id);

						DBHelper dbHelper = new DBHelper(getBaseContext());
						SQLiteDatabase db = dbHelper.getWritableDatabase();

						Cursor c = db.query("news", null, null, null, null,
								null, null);

						if (c.moveToFirst()) {

							int IDCoIndex = c.getColumnIndex("ID");
							int nameCoIndex = c.getColumnIndex("name");
							int descriptionCoIndex = c
									.getColumnIndex("description");
							int url_pictureCoIndx = c
									.getColumnIndex("url_picture");
							int checkedCoIndex = c.getColumnIndex("checked");
							int is_postingCoIndex = c.getColumnIndex("is_posting");

							do {
								boolean checkedFlag = (c.getInt(checkedCoIndex) == 1);
								boolean is_posting  = (c.getInt(is_postingCoIndex) == 1);

								/*
								 * если был выбран пункт и он не был запостен то сделать пост
								 * и пометить в базе данных
							 	*/
								if (checkedFlag && !is_posting){
									makePost(null,
											c.getString(descriptionCoIndex));
									
									ContentValues cv = new ContentValues();
									

									String id = String.valueOf(c.getInt(IDCoIndex));
									cv.put("is_posting", true);

									// обновляем по id
									int updCount = db.update("news", cv, "id = ?",
											new String[] { id });
									Log.d("LOG", "updated rows count = " + updCount);
								}

									

							} while (c.moveToNext());
						} else
							Toast.makeText(getBaseContext(), "0 rows",
									Toast.LENGTH_LONG).show();
						c.close();

						/*						
						 */
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

				/*
				 * Intent i = new Intent( Intent.ACTION_VIEW, Uri.parse(String
				 * .format("https://vk.com/"+user_id+"_%s", ((VKWallPostResult)
				 * response.parsedModel).post_id)));
				 */
				startActivity(i);
			}

			@Override
			public void onError(VKError error) {
				// showError(error.apiError != null ? error.apiError : error);
			}
		});
	}
}
