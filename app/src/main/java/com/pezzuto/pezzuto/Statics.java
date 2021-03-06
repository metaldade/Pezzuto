package com.pezzuto.pezzuto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v4.util.ArraySet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pezzuto.pezzuto.items.Event;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.requests.RequestsUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/**
 * Created by dade on 21/04/17.
 */

public class Statics {
    public static final String SHARED_PREF = "pezzuto";
    public static final int CALENDAR_PERMISSION = 1;
    public static final String PROMOZIONI_JSON =
            "[{\"id\":23,\"nome\":\"Tempora consequatur nesciunt maxime ad ipsam ullam nihil enim eum vel enim amet.\",\"descrizione\":\"Facere vel aut quibusdam numquam eligendi quo provident. Dolore repudiandae molestias possimus sint. Debitis aut sunt ex qui et odio. Ullam deserunt consectetur facilis incidunt. Et aut aut voluptatem voluptatem.\",\"valida_dal\":\"2017-10-24\",\"valida_al\":\"2017-12-06\",\"esaurimento_scorte\":\"0\",\"img\":\"http://lorempixel.com/640/480/?41188\",\"id_categoria\":\"11\",\"attiva\":\"1\",\"id_utente\":\"1\",\"created_at\":\"2017-03-22 14:36:20\",\"updated_at\":\"2017-03-22 14:36:20\"}]";

    public static Date parseSimpleDate(String target) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
            return df.parse(target);
        }
        catch (ParseException ex) { return null; }
    }

    public static Date parseExtDate(String target) {
        try {
            DateFormat df = new SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ITALIAN);
            return df.parse(target);
        }
        catch (ParseException ex) { return null; }
    }
    public static String getSimpleDate(Date d) {
        DateFormat df = new SimpleDateFormat("dd/MM/yy",Locale.ITALIAN);
        return removeZeroes(df.format(d));
    }
    public static String getDayMonth(Date d) {
        DateFormat df = new SimpleDateFormat("dd MMMM",Locale.ITALIAN);
        return removeZeroes(df.format(d));
    }
    private static String removeZeroes(String s) {
        return s.replaceFirst("^0+(?!$)", "");
    }
    public static String getExtDate(Date d) {
        DateFormat df = new SimpleDateFormat("dd-MM-yy HH:mm:ss",Locale.ITALIAN);
        return df.format(d);
    }
    public static void loadImage(Context context, String path, final ImageView v) {
        Picasso.with(context).load(path.contains("http") ? path: RequestsUtils.BASE_STORAGE_URL+path).into(v);
    }
    public static void loadImage(Context context, String path, final ImageView v, Callback callback) {
        Picasso.with(context).load(path.contains("http") ? path: RequestsUtils.BASE_STORAGE_URL+path).into(v,callback);
    }

    public static String getFormattedEventDate(Event e) {
        return e.getEndDate() == null ? Statics.getDayMonth(e.getStartDate()) :
                "Dal "+removeZeroes(Statics.getDayMonth(e.getStartDate()))+" al "+removeZeroes(Statics.getDayMonth(e.getEndDate()));
    }
    public static void saveImageToExternal(Bitmap bm, String type, int id) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/req_images");
        myDir.mkdirs();
        String fname = type+"-"+id;
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void swap(String[] array,int i1,int i2) {
        String temp = array[i2];
        array[i2] = array[i1];
        array[i1] = temp;
    }
    public static double privateSurplus(double price) {
        return price + price*(10.52)/100;
    }
    public static double getFinalPrice(Context c, Product p) {
        if (p.getCategory().toLowerCase().equals("pellet"))
            return p.getPromotionPrice() == 0 ? p.getPrice() : p.getPromotionPrice();
        else if (SharedUtils.isPrivateMember(c))
            return p.getPromotionPrice() == 0 ? privateSurplus(p.getPrice()) : privateSurplus(p.getPromotionPrice());
        else return p.getPromotionPrice() == 0 ? p.getPrice() : p.getPromotionPrice();
    }
    public static double getFinalOriginalPrice(Context c, Product p) {
        if (p.getCategory().toLowerCase().equals("pellet"))
            return p.getPrice();
        else if (SharedUtils.isPrivateMember(c))
            return privateSurplus(p.getPrice());
        else return p.getPrice();
    }
    public static void sendMail(Context context, String mail, String subject) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ mail});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        context.startActivity(Intent.createChooser(intent, "Send Email"));
    }

}
