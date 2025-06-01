//package com.example.realestate.ui.connection;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.example.realestate.data.repository.ApiRepository;
//
//public class ConnectApiViewModel extends ViewModel {
//
//    private final ApiRepository apiRepository;
//
//    public enum ConnectionState { LOADING, CONNECTED, FAILED }
//
//    private final MutableLiveData<ConnectionState> _connectionState = new MutableLiveData<>();
//    public LiveData<ConnectionState> connectionState = _connectionState;
//
//    public ConnectApiViewModel(ApiRepository repository) {
//        this.apiRepository = repository;
//    }
//
//    public void connect() {
//        _connectionState.setValue(ConnectionState.LOADING);
//
//        apiRepository.ping(new ApiRepository.PingCallback() {
//            @Override
//            public void onSuccess() {
//                _connectionState.postValue(ConnectionState.CONNECTED);
//            }
//
//            @Override
//            public void onFailure() {
//                _connectionState.postValue(ConnectionState.FAILED);
//            }
//        });
//    }
//}
