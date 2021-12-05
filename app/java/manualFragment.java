package com.example.navigationdrawer1;

import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;

public class manualFragment extends Fragment {

    PDFView pdfView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflate Layout for User Manual
        View view = inflater.inflate(R.layout.fragment_manual, container, false);

        pdfView = (PDFView) view.findViewById(R.id.pdfView);
        pdfView.fromAsset("PGE_UserManual.pdf").load();

        return view;
    }
}
