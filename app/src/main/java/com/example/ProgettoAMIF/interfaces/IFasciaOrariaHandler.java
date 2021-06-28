package com.example.ProgettoAMIF.interfaces;

import com.example.ProgettoAMIF.fasciaoraria.data.FasciaOraria;

public interface IFasciaOrariaHandler {

    public void initFasceOrarie();
    public void saveFasceOrarie();
    public int getSize();
    public FasciaOraria getFasciaOraria(int index);
    public FasciaOraria getFasciaOrariaByID(int ID);
    public FasciaOraria getFasciaOrariaByName(String name);
    public void addFasciaOraria(FasciaOraria fasciaOraria);
    public void deleteFasciaOraria(int index);
    public void deleteFasciaOrariaByID(int ID);
    public void deleteFasciaOrariaByName(String name);
    public void deleteAll();
    public void enableFasciaOraria(int ID);
    public void disableFasciaOraria(int ID);

}
