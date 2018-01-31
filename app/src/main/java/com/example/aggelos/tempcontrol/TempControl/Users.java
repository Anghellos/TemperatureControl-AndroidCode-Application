package com.example.aggelos.tempcontrol.TempControl;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.aggelos.tempcontrol.AzureConnection.DatabaseConnection;
import com.example.aggelos.tempcontrol.Domain.User;
import com.example.aggelos.tempcontrol.R;

import java.util.ArrayList;

public class Users extends Fragment {

    private final int SEEK_BAR_MIN = 15;
    private final int SEEK_BAR_STEP = 1;

    private View _view;

    private User _user;
    private User _user_selected_list_view;

    private Spinner _user_spinner;
    private ListView _user_list_view;
    private EditText _temp_text;
    private SeekBar _temp_seekBar;
    private FloatingActionButton _user_options_btn;

    private LinearLayout _add_user_layout;
    private LinearLayout _edit_user_layout;
    private LinearLayout _delete_user_layout;

    public Users() {
        // Required empty public constructor
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_users, container, false);

        _user_spinner = _view.findViewById(R.id.user_spinner);
        _user_list_view = _view.findViewById(R.id.user_list_view);
        _temp_text = _view.findViewById(R.id.add_user_temp_txt);
        _temp_seekBar = _view.findViewById(R.id.add_user_seekBar);

        initializeSpinner();
        initializeListView();
        initializeMenuBtn();
        initializeSeekBar();

        updateSpinner();
        updateListView();
        updateSeekBar();

        moveUser();

        return _view;
    }

    private void initializeMenuBtn() {
        _add_user_layout = _view.findViewById(R.id.add_user_layout);
        _edit_user_layout = _view.findViewById(R.id.edit_user_layout);
        _delete_user_layout = _view.findViewById(R.id.delete_user_layout);

        _user_options_btn = _view.findViewById(R.id.btn_user_option);
        FloatingActionButton user_add_btn = _view.findViewById(R.id.btn_user_add);
        FloatingActionButton user_edit_btn = _view.findViewById(R.id.btn_user_edit);
        FloatingActionButton user_delete_btn = _view.findViewById(R.id.btn_user_delete);

        _user_options_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMenuShowHideBtn(v);
            }
        });

        user_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddUserDialog(v);
            }
        });

        user_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditUserDialog(v);
            }
        });

        user_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteUserDialog(v);
            }
        });
    }

    private void initializeListView() {
        _user_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                _user_selected_list_view = new User();
                _user_selected_list_view.setName(_user_list_view.getItemAtPosition(position).toString());
                DatabaseConnection databaseConnection = new DatabaseConnection();
                databaseConnection.getUser(_user_selected_list_view);
            }
        });
    }

    private void initializeSeekBar() {
        int seek_bar_max = 35;
        _temp_seekBar.setMax((seek_bar_max - SEEK_BAR_MIN) / SEEK_BAR_STEP);
        _temp_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = SEEK_BAR_MIN + (progress * SEEK_BAR_STEP);
                _temp_text.setText(String.valueOf(value) + " °C");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                _user.setOptimTemp(Integer.parseInt(_temp_text.getText().toString().substring(0, 2)));
                DatabaseConnection databaseConnection = new DatabaseConnection();
                databaseConnection.updateUser(_user);
                Toast.makeText(getActivity().getApplicationContext(), "Temperature has changed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initializeSpinner() {
        _user_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                _user.setName(_user_spinner.getSelectedItem().toString());
                DatabaseConnection databaseConnection = new DatabaseConnection();
                databaseConnection.getUser(_user);
                updateSeekBar();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void setMenuShowHideBtn(View view) {
        if (_add_user_layout.getVisibility() != view.VISIBLE && _edit_user_layout.getVisibility() != view.VISIBLE &&
                _delete_user_layout.getVisibility() != view.VISIBLE) {
            _add_user_layout.setVisibility(view.VISIBLE);
            _edit_user_layout.setVisibility(view.VISIBLE);
            _delete_user_layout.setVisibility(view.VISIBLE);

            startAnimation();
        } else {
            _add_user_layout.setVisibility(view.GONE);
            _edit_user_layout.setVisibility(view.GONE);
            _delete_user_layout.setVisibility(view.GONE);

            endAnimation();
        }
    }

    private void moveUser() {
        Button up = _view.findViewById(R.id.user_up_btn);
        Button down = _view.findViewById(R.id.user_down_btn);

        up.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                User swipe_user = new User();
                if (_user_selected_list_view.getPriority() != 1) {
                    swipe_user.setPriority(_user_selected_list_view.getPriority() - 1);
                    DatabaseConnection databaseConnection = new DatabaseConnection();
                    databaseConnection.getUser(swipe_user);
                    swipe_user.setPriority(swipe_user.getPriority() + 1);
                    _user_selected_list_view.setPriority(_user_selected_list_view.getPriority() - 1);
                    databaseConnection.updateUser(swipe_user);
                    databaseConnection.updateUser(_user_selected_list_view);
                    updateSpinner();
                    updateListView();
                    Toast.makeText(getActivity().getApplicationContext(), "User has changed position", Toast.LENGTH_LONG).show();
                }
            }
        });

        down.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                User swipe_user = new User();
                swipe_user.setPriority(_user_selected_list_view.getPriority() + 1);
                DatabaseConnection databaseConnection = new DatabaseConnection();
                databaseConnection.getUser(swipe_user);
                if (swipe_user.getName() != null) {
                    swipe_user.setPriority(swipe_user.getPriority() - 1);
                    _user_selected_list_view.setPriority(_user_selected_list_view.getPriority() + 1);
                    databaseConnection.updateUser(swipe_user);
                    databaseConnection.updateUser(_user_selected_list_view);
                    updateSpinner();
                    updateListView();
                    Toast.makeText(getActivity().getApplicationContext(), "User has changed position", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showAddUserDialog(View view) {
        setMenuShowHideBtn(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(_view.getContext());
        View mView = getLayoutInflater().inflate(R.layout.add_user_dialog, null);
        builder.setView(mView);
        builder.create();
        final AlertDialog alertDialog = builder.show();

        final EditText ADD_NAME_TEXT = mView.findViewById(R.id.add_user_txt);
        final EditText ADD_TEMP_TEXT = mView.findViewById(R.id.add_user_temp_txt);
        SeekBar add_temp_seekBar = mView.findViewById(R.id.add_user_seekBar);
        final Button CREATE_BTN = mView.findViewById(R.id.add_user_create_btn);
        final Button CANCEL_BTN = mView.findViewById(R.id.add_user_cancel_btn);

        CREATE_BTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                User user = new User();
                user.setName(ADD_NAME_TEXT.getText().toString());
                user.setOptimTemp(Integer.parseInt(ADD_TEMP_TEXT.getText().toString().substring(0, 2)));

                DatabaseConnection databaseConnection = new DatabaseConnection();
                databaseConnection.addUser(user);
                updateSpinner();
                updateListView();
                alertDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "User has been created", Toast.LENGTH_LONG).show();
            }
        });

        CANCEL_BTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        final int SEEK_BAR_MAX = 35;
        add_temp_seekBar.setMax((SEEK_BAR_MAX - SEEK_BAR_MIN) / SEEK_BAR_STEP);
        add_temp_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = SEEK_BAR_MIN + (progress * SEEK_BAR_STEP);
                ADD_TEMP_TEXT.setText(String.valueOf(value) + " °C");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void showEditUserDialog(View view) {
        setMenuShowHideBtn(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(_view.getContext());
        View mView = getLayoutInflater().inflate(R.layout.edit_user_dialog, null);
        builder.setView(mView);
        builder.create();
        final AlertDialog ALERT_DIALOG = builder.show();

        final EditText EDIT_NAME_TEXT = mView.findViewById(R.id.edit_user_txt);
        EDIT_NAME_TEXT.setText(_user.getName());

        final Button SAVE_BTN = mView.findViewById(R.id.edit_user_save_btn);
        SAVE_BTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatabaseConnection connectionDatabase = new DatabaseConnection();
                _user.setName(EDIT_NAME_TEXT.getText().toString());
                connectionDatabase.updateUser(_user);

                updateSpinner();
                updateListView();
                ALERT_DIALOG.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "User has been changed", Toast.LENGTH_LONG).show();
            }
        });

        final Button CANCEL_BTN = mView.findViewById(R.id.edit_user_cancel_btn);
        CANCEL_BTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ALERT_DIALOG.dismiss();
            }
        });
    }

    private void showDeleteUserDialog(View view) {
        setMenuShowHideBtn(view);

        final AlertDialog.Builder BUILDER = new AlertDialog.Builder(_view.getContext());
        View mView = getLayoutInflater().inflate(R.layout.delete_user_dialog, null);
        BUILDER.setView(mView);
        BUILDER.create();
        final AlertDialog ALERT_DIALOG = BUILDER.show();

        final Button DELETE_BTN = mView.findViewById(R.id.delete_user_delete_btn);
        DELETE_BTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatabaseConnection connectionDatabase = new DatabaseConnection();
                connectionDatabase.deleteUser(_user);

                updateSpinner();
                updateListView();
                ALERT_DIALOG.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "User has been deleted", Toast.LENGTH_LONG).show();
            }
        });

        final Button CANCEL_BTN = mView.findViewById(R.id.delete_user_cancel_btn);
        CANCEL_BTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ALERT_DIALOG.dismiss();
            }
        });
    }

    private void updateSpinner() {
        DatabaseConnection connectionDatabase = new DatabaseConnection();

        ArrayList<String> users_list = connectionDatabase.getUsersList();
        ArrayAdapter<String> users_list_spinner;
        users_list_spinner = new ArrayAdapter<String>(_view.getContext(), R.layout.custom_spinner, users_list);
        _user_spinner.setAdapter(users_list_spinner);

        _user = new User();
        _user.setName(_user_spinner.getSelectedItem().toString());
        connectionDatabase.getUser(_user);
    }

    private void updateListView() {
        DatabaseConnection connectionDatabase = new DatabaseConnection();

        ArrayList<String> users_list = connectionDatabase.getUsersList();
        ArrayAdapter<String> users_list_spinner;
        users_list_spinner = new ArrayAdapter<String>(_view.getContext(), R.layout.custom_list_view, users_list);
        _user_list_view.setAdapter(users_list_spinner);

        _user = new User();
        _user.setName(_user_spinner.getSelectedItem().toString());
        connectionDatabase.getUser(_user);
    }

    private void updateSeekBar() {
        DatabaseConnection connectionDatabase = new DatabaseConnection();

        _user = new User();
        _user.setName(_user_spinner.getSelectedItem().toString());
        connectionDatabase.getUser(_user);
        _temp_text.setText(String.valueOf(_user.getOptimTemp()) + " °C");
        _temp_seekBar.setProgress(_user.getOptimTemp() - SEEK_BAR_MIN);
    }

    private void startAnimation() {
        Animation show_button = AnimationUtils.loadAnimation(getActivity()
                .getBaseContext().getApplicationContext(), R.anim.show_button);
        Animation show_layout = AnimationUtils.loadAnimation(getActivity()
                .getBaseContext().getApplicationContext(), R.anim.show_layout);

        _add_user_layout.startAnimation(show_layout);
        _edit_user_layout.startAnimation(show_layout);
        _delete_user_layout.startAnimation(show_layout);
        _user_options_btn.startAnimation(show_button);
    }

    private void endAnimation() {
        Animation hide_button = AnimationUtils.loadAnimation(getActivity()
                .getBaseContext().getApplicationContext(), R.anim.hide_button);

        Animation hide_layout = AnimationUtils.loadAnimation(getActivity()
                .getBaseContext().getApplicationContext(), R.anim.hide_layout);

        _add_user_layout.startAnimation(hide_layout);
        _edit_user_layout.startAnimation(hide_layout);
        _delete_user_layout.startAnimation(hide_layout);
        _user_options_btn.startAnimation(hide_button);
    }
}
