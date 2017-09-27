package com.pezzuto.pezzuto;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pezzuto.pezzuto.adapter.CategoriesListViewAdapter;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.pezzuto.pezzuto.ui.GraphicUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class BottomFilterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnFragmentInteractionListener mListener;
    private ListView categories;
    private TextView primoPianoButton;
    public BottomFilterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BottomFilterFragment.
     */
    // TODO: Rename and change types and number of parameters
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    String[] cats;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bottom_filter, container, false);
        categories = (ListView) v.findViewById(R.id.categories);
        primoPianoButton = (TextView) v.findViewById(R.id.primoPiano);
        cats = new String[mListener.getCategories().keySet().size()];
        mListener.getCategories().keySet().toArray(cats);
        ImageView done = (ImageView) v.findViewById(R.id.done1);
        if (!mListener.getSelectedCategory().equals("")) {
            primoPianoButton.setTypeface(Typeface.DEFAULT);
            done.setVisibility(View.GONE);
        }
        //Individua primo piano e rimuovi
        Arrays.sort(cats);
        int primoPiano = Arrays.binarySearch(cats,"In primo piano");
        cats = removeIndex(cats,primoPiano);

        primoPianoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int identifier = mListener.getCategories().get("In primo piano");
                mListener.setSelectedCategory("");
                mListener.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_HIDDEN);
                mListener.launchProductFragment(identifier);
            }
        });
        final CategoriesListViewAdapter adapter = new CategoriesListViewAdapter(getContext(),
                R.layout.category_list_item, cats, mListener.getSelectedCategory());
        categories.setAdapter(adapter);
        categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int identifier = mListener.getCategories().get(cats[position]);
                mListener.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_HIDDEN);
                mListener.launchProductFragment(identifier);
                mListener.setSelectedCategory(cats[position]);
            }
        });
        GraphicUtils.setListViewHeightBasedOnChildren(categories,0);
        return v;
    }
    public String[] removeIndex(String[] original, int index) {
        String[] arr = new String[original.length-1];
        for (int i = 0; i < index; i++)
            arr[i] = original[i];
        for (int i = index+1; i < original.length; i++)
            arr[i-1] = original[i];
        return arr;
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
