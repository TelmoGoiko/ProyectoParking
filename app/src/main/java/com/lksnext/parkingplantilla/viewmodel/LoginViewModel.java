package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.IVehicleRepository;
import com.lksnext.parkingplantilla.domain.Callback;

public class LoginViewModel extends ViewModel {

    // Aquí puedes declarar los LiveData y métodos necesarios para la vista de inicio de sesión
    MutableLiveData<Boolean> logged = new MutableLiveData<>(null);
    private MutableLiveData<String> userEmail = new MutableLiveData<>();
    private final IVehicleRepository dataRepository;

    public LoginViewModel() {
        this(com.lksnext.parkingplantilla.data.DataRepository.getInstance());
    }
    // Constructor para test
    protected LoginViewModel(IVehicleRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public LiveData<Boolean> isLogged(){
        return logged;
    }

    public void loginUser(String userOrEmail, String password) {
        if (userOrEmail.contains("@")) {
            // Es un email
            dataRepository.login(userOrEmail, password, new Callback() {
                @Override
                public void onSuccess() {
                    logged.setValue(Boolean.TRUE);
                }
                @Override
                public void onFailure() {
                    logged.setValue(Boolean.FALSE);
                }
            });
        } else {
            // Es un nombre de usuario, buscar el email en el repositorio
            dataRepository.findUserByUsername(userOrEmail, new Callback() {
                @Override
                public void onSuccess() {
                    // El email se ha guardado en userEmail
                    String email = userEmail.getValue();
                    if (email != null) {
                        dataRepository.login(email, password, new Callback() {
                            @Override
                            public void onSuccess() {
                                logged.setValue(Boolean.TRUE);
                            }
                            @Override
                            public void onFailure() {
                                logged.setValue(Boolean.FALSE);
                            }
                        });
                    } else {
                        logged.setValue(Boolean.FALSE);
                    }
                }
                @Override
                public void onFailure() {
                    logged.setValue(Boolean.FALSE);
                }
            }, userEmail);
        }
    }
}
