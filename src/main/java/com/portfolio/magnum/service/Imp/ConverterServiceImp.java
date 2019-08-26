package com.portfolio.magnum.service.Imp;

import com.portfolio.magnum.service.ConverterService;
import org.springframework.stereotype.Service;

@Service
public class ConverterServiceImp implements ConverterService {

    @Override
    public byte[] getVideoFileConverted(byte[] source, String extension) {
        return new byte[0];
    }
}
