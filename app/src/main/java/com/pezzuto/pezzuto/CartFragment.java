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
import android.widget.ListView;

import com.pezzuto.pezzuto.adapter.CartListViewAdapter;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.ui.GraphicUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    ListView cart;
    SharedPreferences shre;
    SharedPreferences.Editor edit;
    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_cart, container, false);
        cart = (ListView) v.findViewById(R.id.listViewCart);
        GraphicUtils.setListViewHeightBasedOnChildren(cart);
        shre = getContext().getSharedPreferences(Statics.SHARED_PREF+"-cart", Context.MODE_PRIVATE);
        edit = shre.edit();
        List<Product> prods = new ArrayList<>();
        Set<String> products = shre.getStringSet("products", new ArraySet<String>());
        prods = SharedUtils.getProductsFromCart(getContext());
        //Set adapter
        CartListViewAdapter adapter = new CartListViewAdapter(getContext(),R.layout.cart_list_item,prods);
        cart.setAdapter(adapter);
        return v;
    }



}
