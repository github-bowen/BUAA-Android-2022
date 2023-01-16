package com.example.success.ui.notifications;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.success.R;
import com.example.success.ShowTask;
import com.example.success.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EditText search = getActivity().findViewById(R.id.search_knowledge);
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // 这两个条件必须同时成立，如果仅仅用了enter判断，就会执行两次
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    String keyOrLabel = search.getText().toString();
                    System.out.println(keyOrLabel);
                    if (keyOrLabel.equals("")) {
                        dialog.setMessage("您还没有输入任何内容哦");
                        dialog.show();
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("keyOrLabel",keyOrLabel);
                        bundle.putBoolean("fromSearch",true);
                        Intent intent = new Intent(getActivity(), ShowTask.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
    }
}

