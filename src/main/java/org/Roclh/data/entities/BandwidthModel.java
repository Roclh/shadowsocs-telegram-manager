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
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class BandwidthModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_model_id")
    private UserModel userModel;

    private Bandwidth bandwidth;

    @Getter
    public enum Bandwidth{
        MB4("4mbit"), MB8("8mbit"), MB16("16mbit"), MB32("32mbit");

        private final String bandwidth;
        Bandwidth(String bandwidth){
            this.bandwidth = bandwidth;
        }


    }
}
