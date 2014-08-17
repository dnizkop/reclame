package com.reclame.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
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
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.model.VKWallPostResult;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;

public class PostToWall extends Activity {

	static String user_id = null;

	private Bitmap getPhoto(String pathName) {
		Bitmap b = null;

		b = BitmapFactory.decodeFile(pathName);

		return b;
	}

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
							int is_postingCoIndex = c
									.getColumnIndex("is_posting");

							do {
								boolean checkedFlag = (c.getInt(checkedCoIndex) == 1);
								boolean is_posting = (c
										.getInt(is_postingCoIndex) == 1);

								String url = c.getString(url_pictureCoIndx);
								/*
								 * если был выбран пункт и он не был запостен то
								 * сделать пост и пометить в базе данных
								 */

								if (checkedFlag && !is_posting) {

									String mas[] = url.split("/");
									url = mas[mas.length - 1];

									String folderToSave = Environment
											.getExternalStorageDirectory()
											.toString();

									url = folderToSave + "/" + url;
									
									AsynhPost post = new AsynhPost();
									post.execute(new Pair(url,c.getString(descriptionCoIndex)));


									ContentValues cv = new ContentValues();

									String id = String.valueOf(c
											.getInt(IDCoIndex));
									cv.put("is_posting", true);

									// обновляем по id
									int updCount = db.update("news", cv,
											"id = ?", new String[] { id });
									Log.d("LOG", "updated rows count = "
											+ updCount);
								}

							} while (c.moveToNext());
						} else
							Toast.makeText(getBaseContext(), "0 rows",
									Toast.LENGTH_LONG).show();
						c.close();
					}
				});

	}

	class AsynhPost extends AsyncTask<Pair, Void, Void> {

		String url;
		String text;
		@Override
		protected Void doInBackground(Pair... params) {

			Pair p = params[0];

			url = (String) p.first;
			text = (String) p.second;

			final Bitmap photo = getPhoto(url);
			VKRequest request = VKApi.uploadWallPhotoRequest(new VKUploadImage(
					photo, VKImageParameters.jpgImage(0.9f)), 0, 60479154);
			request.executeWithListener(new VKRequestListener() {
				@Override
				public void onComplete(VKResponse response) {
					photo.recycle();
					VKApiPhoto photoModel = ((VKPhotoArray) response.parsedModel)
							.get(0);
					makePost(new VKAttachments(photoModel), text);
				}

				@Override
				public void onError(VKError error) {
				}
			});

			// TODO Auto-generated method stub
			return null;
		}

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
