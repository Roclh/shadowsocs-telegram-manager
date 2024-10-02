package org.Roclh.data.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.Roclh.bot.TelegramBotProperties;
import org.Roclh.data.entities.LocalizationModel;
import org.Roclh.data.repositories.LocalizationRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LocalizationService {
    private final LocalizationRepository localizationRepository;
    private final TelegramBotProperties telegramBotProperties;

    @NonNull
    public Locale getOrCreate(@NonNull Long telegramId){
        if(localizationRepository.existsById(telegramId)){
            return localizationRepository.findById(telegramId).map(LocalizationModel::getLocale).map(Locale::forLanguageTag).orElse(Locale.forLanguageTag(telegramBotProperties.getDefaultLocale()));
        }else {
            LocalizationModel localizationModel = LocalizationModel.builder()
                    .telegramId(telegramId)
                    .locale(telegramBotProperties.getDefaultLocale())
                    .build();
            return Locale.forLanguageTag(localizationRepository.saveAndFlush(localizationModel).getLocale());
        }
    }
}
