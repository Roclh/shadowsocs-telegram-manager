package org.Roclh.data.model.manager;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.repositories.ManagerRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManagerService {
    private final ManagerRepository managerRepository;

    public boolean addManager(@NonNull ManagerModel managerModel){
        try{
            managerRepository.save(managerModel);
            return true;
        }catch (Exception e){
            log.error("Failed to add manager", e);
            return false;
        }
    }

    @Nullable
    public ManagerModel getManager(String managerId){
        return managerRepository.findByTelegramId(managerId);
    }

    public List<ManagerModel> getManagers(){
        return managerRepository.findAll();
    }
    public boolean delManager(@NonNull String telegramId){
        return managerRepository.deleteByTelegramId(telegramId) > 0;
    }
}
