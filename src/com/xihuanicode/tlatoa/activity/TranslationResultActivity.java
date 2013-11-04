package com.xihuanicode.tlatoa.activity;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.xihuanicode.tlatoa.customlistview.LazyAdapter;
import com.xihuanicode.tlatoa.utils.XMLParser;

public class TranslationResultActivity extends Activity {
	
	TranslationResultActivity c = this;

	private String phrase;
	
	// All static variables
	static final String URL = "http://api.androidhive.info/music/music.xml";

	// XML node keys
	public static final String KEY_SONG = "song"; // parent node
	public static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_ARTIST = "artist";
	public static final String KEY_DURATION = "duration";
	public static final String KEY_THUMB_URL = "thumb_url";

	private ImageSwitcher imageSwitcher;
	private ListView list;
	private LazyAdapter adapter;
	private ArrayList<HashMap<String, String>> songsList;
	private XMLParser parser;
	private String xml;
	private Document doc;
	private NodeList nl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.translation_result);
		
		// Get views
		list = (ListView) findViewById(R.id.list);
		imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
		
		phrase = getIntent().getStringExtra("phrase");
		
		new Translate().execute(phrase);		
	}
	
	private class Translate extends AsyncTask<String, Integer, Bitmap[]> {

		@Override
		protected Bitmap[] doInBackground(String... phrase) {
			
			Bitmap [] photos = new Bitmap[2];
			
			// TODO: Candidate code to send for reporting
			try {

				
				String [] urls =
				{
						"https://lh5.googleusercontent.com/-cIF3t_2FGi8/UnQ2vsKalaI/AAAAAAAADO0/3V4OTAFQlYg/w346-h408/hello_1.png",
						"https://lh5.googleusercontent.com/-i3gSLh8QAfE/UnQ4wAQU1aI/AAAAAAAADPY/X05Vf1cTNQ8/w346-h408/hello_2.png"
				};
				
				
				int i = 0;
				for (String url : urls) {

					URL murl = new URL(url);

					HttpGet httpRequest = null;

					httpRequest = new HttpGet(murl.toURI());

					HttpClient httpclient = new DefaultHttpClient();
					HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);

					HttpEntity entity = response.getEntity();
					BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
					InputStream input = b_entity.getContent();

					Bitmap bitmap = BitmapFactory.decodeStream(input);

					photos[i] = bitmap;

					i = i++;

				}
				
				
				
				songsList = new ArrayList<HashMap<String, String>>();		
				parser = new XMLParser();
				xml = parser.getXmlFromUrl(URL); // getting XML from URL
				doc = parser.getDomElement(xml); // getting DOM element
				nl = doc.getElementsByTagName(KEY_SONG);

			} catch (Exception e) {

			}
			
			return photos;
		}

		@Override
		protected void onPostExecute(Bitmap[] images) {

			imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
			imageSwitcher.setFactory(new ViewFactory() {
				@Override
				public View makeView() {
					ImageView imageView = new ImageView(getApplicationContext());
					return imageView;
				}
			});
			imageSwitcher.setInAnimation(getApplicationContext(), android.R.anim.fade_in);
			imageSwitcher.setOutAnimation(getApplicationContext(), android.R.anim.fade_out);
			
			
			@SuppressWarnings("deprecation")
			final Drawable drawable1 =new BitmapDrawable(images[0]);
			@SuppressWarnings("deprecation")
			final Drawable drawable2 =new BitmapDrawable(images[1]);
			
			imageSwitcher.postDelayed(new Runnable() {
				int i = 0;

				public void run() {
					imageSwitcher.setImageDrawable(i++ % 2 == 0 ? drawable1 : drawable2);
					imageSwitcher.postDelayed(this, 500);
				}
			}, 500);
			
			
			// looping through all song nodes <song>
			for (int i = 0; i < nl.getLength(); i++) {
				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();
				Element e = (Element) nl.item(i);
				// adding each child node to HashMap key => value
				map.put(KEY_ID, parser.getValue(e, KEY_ID));
				map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
				map.put(KEY_ARTIST, parser.getValue(e, KEY_ARTIST));
				map.put(KEY_DURATION, parser.getValue(e, KEY_DURATION));
				map.put(KEY_THUMB_URL, parser.getValue(e, KEY_THUMB_URL));

				// adding HashList to ArrayList
				songsList.add(map);
			}

			// Getting adapter by passing xml data ArrayList
			adapter = new LazyAdapter(c, songsList);
			list.setAdapter(adapter);

			// Click event for single list row
			list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Toast.makeText(getApplicationContext(), "Clicked " + position, Toast.LENGTH_SHORT).show();
				}
			});
			
			

		}
	 

	}
		

}