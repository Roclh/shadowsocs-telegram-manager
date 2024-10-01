package org.Roclh.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BandwidthModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    @NonNull
    @OneToOne
    @JoinColumn(name = "user_model_id")
    private UserModel userModel;

    @Nullable
    private Bandwidth bandwidth;

    @Getter
    public enum Bandwidth{
        MB4("4mbit"), MB8("8mbit"), MB16("16mbit"), MB32("32mbit"), MB64("64mbit");
        public String getBurst(){
            return (Long.parseLong(this.bandwidth.replaceAll("[a-zA-Z]", "")) / 4L) + "m";
        }
        private final String bandwidth;
        Bandwidth(String bandwidth){
            this.bandwidth = bandwidth;
        }


    }
}
