package com.beastek.eol.ui.patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.beastek.eol.R;

public class ViewTaskActivityFragment extends Fragment {

    private int id;
    public ViewTaskActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_view_task, container, false);

        return rootView;
    }
}
