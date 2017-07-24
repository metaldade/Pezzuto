package com.pezzuto.pezzuto;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.pezzuto.pezzuto.requests.RequestsUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductDetailFragment extends RefreshableFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private Product p;
private SharedPreferences shre;
    private SharedPreferences.Editor edit;
    private OnFragmentInteractionListener mListener;
    TextView category;
    TextView title;
    TextView description;
    TextView marca;
    TextView price;
    ImageView image;
    public ProductDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductDetailFragment newInstance(String param1, String param2) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        p = mListener.getSelectedProduct();
        shre = getContext().getSharedPreferences(Statics.SHARED_PREF+"-cart",Context.MODE_PRIVATE);
        edit = shre.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_product_detail, container, false);
        category = (TextView) v.findViewById(R.id.category);
        title = (TextView) v.findViewById(R.id.title);
        description = (TextView) v.findViewById(R.id.description);
        marca = (TextView) v.findViewById(R.id.marca);
        price = (TextView) v.findViewById(R.id.price);
        image = (ImageView) v.findViewById(R.id.image);
        mListener.setFabVisible(true);
        mListener.disableSwipeRefresh();
        if (p == null) return v;
        fill();
        if (checkIfInCart()) setFabRemove();
        else setFabAdd();
        return v;
    }
    private void addToCart() {
       SharedUtils.addToCart(getContext(),p);
        mListener.setCartIconFull();
        setFabRemove();
    }
    private void removeFromCart() {
        SharedUtils.removeFromCart(getContext(),p);
        if (SharedUtils.isCartEmpty(getContext())) mListener.setCartIconEmpty();
        setFabAdd();
    }
    public void setFabAdd() {
        mListener.getFab().setIcon(R.drawable.ic_add_shopping_cart);
        mListener.getFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
    }
    public void setFabRemove() {
        mListener.getFab().setIcon(R.drawable.ic_remove_shopping_cart);
        mListener.getFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromCart();
            }
        });
    }
    public boolean checkIfInCart() {
        Set<String> products = shre.getStringSet("products", new ArraySet<String>());
        if (products.contains(""+p.getId())) return true;
        else return false;
    }
    public void fill() {
        category.setText(p.getCategory());
        title.setText(p.getTitle());
        description.setText(p.getDescription());
        marca.setText(p.getMarca());
        price.setText(String.format(Locale.ITALY,"%.2f",p.getPromotionPrice() == 0 ? p.getPrice() : p.getPromotionPrice())+"€ /"+p.getMeasure()+" + "+p.getIVA()+"% IVA");
        Statics.loadImage(getContext(),p.getImage(),image);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public void refresh() {
        RequestsUtils.sendProductRequest(getContext(), p.getId(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    p = ParseUtils.parseProduct(response);
                    fill();
                    mListener.stopRefresh();
                }
                catch (JSONException e) { e.printStackTrace(); }
            }
        });
    }
    public String getType() { return MainActivity.PRODUCT_DETAIL; }
    public boolean hasEmptySet(String type) { return false; }
}
