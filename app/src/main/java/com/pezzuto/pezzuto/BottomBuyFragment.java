package com.pezzuto.pezzuto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.pezzuto.pezzuto.adapter.BuyProductListViewAdapter;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.pezzuto.pezzuto.ui.GraphicUtils;
import com.pezzuto.pezzuto.ui.UiUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BottomBuyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomBuyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private String type;
    private List<Product> prods;
    private OnFragmentInteractionListener mListener;
    private TextView total;
    private TextView iva;
    private TextView imponibile;
    private ListView listViewProd;
    private BuyProductListViewAdapter adapter;
    private boolean isModifying = false;
    public BottomBuyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BottomBuyFragment.
     */
    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        type = args.getString("type");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bottom_buy, container, false);
        //get listView
        listViewProd = (ListView) v.findViewById(R.id.listViewProducts);
        imponibile = (TextView) v.findViewById(R.id.priceImponibile);
        total = (TextView) v.findViewById(R.id.priceTot);
        iva = (TextView) v.findViewById(R.id.priceIva);
        //Set adapter
        if (type.equals("cart")) prods = SharedUtils.getProductsFromCart(getContext());
        else prods = mListener.getSelectedPromprod().getProducts();
        adapter = new BuyProductListViewAdapter(getContext(),R.layout.promotion_buy_list_item,prods,
               imponibile, iva, total);
        listViewProd.setAdapter(adapter);
        if (prods.size() > 0) GraphicUtils.setListViewHeightBasedOnChildren(listViewProd);
        return v;
    }
    public static BottomBuyFragment newInstance(String type) {
        BottomBuyFragment f = new BottomBuyFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("type", type);
        f.setArguments(args);
        return f;
    }
    public void goModify(boolean modify) {
        isModifying = modify;
        for (Product p : prods) {
            p.goModify(modify);
        }
        adapter.notifyDataSetChanged();
    }
    public boolean isModifying() {
        return isModifying;
    }
    public boolean isAtLeastOneChecked() {
        for (Product p : prods)
            if (p.isRemoving()) return true;
        return false;
    }
    public void undoRemoving() {
        for (Product p : prods)
            p.goRemove(false);
    }
    public void removeSelected() {
        List<Product> prodsClone = new ArrayList<>();
        prodsClone.addAll(prods);
        for (Product p : prodsClone) {
            if (p.isRemoving()) {
                prods.remove(p);
                SharedUtils.removeFromCart(getContext(),p);
            }
        }
        adapter.notifyDataSetChanged();
        if (prods.size() > 0) GraphicUtils.setListViewHeightBasedOnChildren(listViewProd);
    }
    public boolean isCartEmpty() {
        return prods.isEmpty();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (type != null && type.equals("cart")) return;
        else if (context instanceof OnFragmentInteractionListener) {
           mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}
