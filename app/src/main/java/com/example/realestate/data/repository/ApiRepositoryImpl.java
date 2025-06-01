//package com.example.realestate.data.repository;
//
//import com.example.realestate.data.api.ApiService;
//import com.example.realestate.data.api.dto.PropertyDTO;
//import com.example.realestate.data.db.dao.PropertyDao;
//import com.example.realestate.data.db.entity.PropertyEntity;
//import com.example.realestate.domain.mapper.PropertyMapper;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Executors;
//
//import retrofit2.*;
//
//public class ApiRepositoryImpl implements ApiRepository {
//    private final ApiService apiService;
//    private final PropertyDao propertyDao;
//
//    public ApiRepositoryImpl(ApiService apiService, PropertyDao propertyDao) {
//        this.apiService = apiService;
//        this.propertyDao = propertyDao;
//    }
//
//    @Override
//    public void fetchAndSaveProperties(Callback<Boolean> callback) {
//        apiService.getProperties().enqueue(new Callback<>() {
//            @Override
//            public void onResponse(Call<List<PropertyDTO>> call, Response<List<PropertyDTO>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<PropertyEntity> entities = new ArrayList<>();
//                    for (PropertyDTO dto : response.body()) {
//                        entities.add(PropertyMapper.toEntity(dto));
//                    }
//                    Executors.newSingleThreadExecutor().execute(() -> {
//                        propertyDao.insertAll(entities);
//                        callback.onResponse(call, Response.success(true));
//                    });
//                } else {
//                    callback.onFailure(call, new Throwable("Unsuccessful response"));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<PropertyDTO>> call, Throwable t) {
//                callback.onFailure(call, t);
//            }
//        });
//    }
//}
