package com.pezzuto.pezzuto;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pezzuto.pezzuto.listeners.OnFirstRunInteractionListener;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.pezzuto.pezzuto.ui.StickyHeaderFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ScreenSlidePageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    OnFirstRunInteractionListener mListener;
    Button goOn;
    TextView text1;
    ImageView slideImage;
    private String type;
    public ScreenSlidePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScreenSlidePageFragment.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public static ScreenSlidePageFragment newInstance(String type) {
        ScreenSlidePageFragment f = new ScreenSlidePageFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("type", type);
        f.setArguments(args);
        return f;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        goOn = (Button) v.findViewById(R.id.goOnButton);
        slideImage = (ImageView) v.findViewById(R.id.slideImage);
        text1 = (TextView) v.findViewById(R.id.text1);
        goOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("remain_updated")) SharedUtils.setNotificationsActive(getContext(),true);
                mListener.goOn();
            }
        });
        //check type
        Bundle args = getArguments();
        type = args.getString("type");

        TextView title = (TextView) v.findViewById(R.id.title);
        TextView content = (TextView) v.findViewById(R.id.text1);
        if (type.equals("welcome")) {
            content.setText(R.string.welcome_text);
            title.setText("Benvenuto nell'app di Pezzuto");
            slideImage.setImageResource(R.drawable.pezzuto);
            goOn.setText("Avanti");
        }
        if (type.equals("remain_updated")) {
            content.setText(R.string.remain_updated_text);
            title.setText("Rimani aggiornato sulle nostre promozioni");
            slideImage.setImageResource(R.drawable.promozioni);
            text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            goOn.setText("Avanti");
        }
        if (type.equals("events")) {
            title.setText("Eventi");
            content.setText(R.string.events_text);
            slideImage.setImageResource(R.drawable.eventi);
            goOn.setText("Avanti");
        }

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFirstRunInteractionListener) {
            mListener = (OnFirstRunInteractionListener) context;
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


}
