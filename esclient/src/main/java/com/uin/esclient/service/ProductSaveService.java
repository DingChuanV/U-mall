package com.uin.esclient.service;

import com.uin.to.es.SpuEsTO;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    boolean productStatusUp(List<SpuEsTO> esTOList) throws IOException;
}
