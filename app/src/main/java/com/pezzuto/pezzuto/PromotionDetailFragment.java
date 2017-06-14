package com.pezzuto.pezzuto;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pezzuto.pezzuto.adapter.BuyProductListViewAdapter;
import com.pezzuto.pezzuto.adapter.PromotionListViewAdapter;
import com.pezzuto.pezzuto.items.Promotion;
import com.pezzuto.pezzuto.items.Promprod;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.pezzuto.pezzuto.requests.RequestsUtils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PromotionDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PromotionDetailFragment extends RefreshableFragment {
    // TODO: Rename parameter arguments, choose names that match

    private Promprod p;

    private OnFragmentInteractionListener mListener;

    public PromotionDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PromotionDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PromotionDetailFragment newInstance(Promprod param1) {
        PromotionDetailFragment fragment = new PromotionDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get selected promotion from MainActivity


    }
    public String getType() {
        return MainActivity.PROMOTION_DETAIL;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        p = mListener.getSelectedPromprod();

        mListener.setFabVisible(true);
        mListener.setPromotionSheetBehaviour();
        mListener.getFab().setIcon(R.drawable.ic_cart);
        mListener.getFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.launchSheet();
            }
        });

        View v = inflater.inflate(R.layout.fragment_promotion_detail, container, false);

        TextView title = (TextView) v.findViewById(R.id.title);
        TextView description = (TextView) v.findViewById(R.id.description);
        TextView validity = (TextView) v.findViewById(R.id.validity);
        ImageView image = (ImageView) v.findViewById(R.id.image);

        //Pupulate fields
        title.setText(p.getTitle());
        description.setText(p.getDescription());
        validity.setText("Promozione valida dal "+Statics.getSimpleDate(p.getValidaDal())+" al "+Statics.getSimpleDate(p.getValidaAl())+(p.isEsaurimento() ? " o esaurimento scorte" : ""));

        //load image
        Statics.loadImage(getContext(),p.getImage(),image);
        Log.d("image:",p.getImage());

        //get listView
        ListView listViewPromo = (ListView) v.findViewById(R.id.listViewPromotions);

        //Set adapter
        PromotionListViewAdapter adapter = new PromotionListViewAdapter(getContext(),R.layout.promotion_list_item,p.getProducts());
        listViewPromo.setAdapter(adapter);

       listViewPromo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               mListener.setSelectedProduct(p.getProducts().get(position));
               mListener.launchFragment(new ProductDetailFragment());
           }
       });


        //set sheet info



        /*listViewPromo.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/

        GraphicUtils.setListViewHeightBasedOnChildren(listViewPromo);
        return v;
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
    public boolean hasEmptySet(String type) { return false; }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public void refresh() {}
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