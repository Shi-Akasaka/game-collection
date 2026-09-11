package com.example.retrogame;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class HardwareDataInitializer implements CommandLineRunner {

    private final HardwareRepository hardwareRepository;

    public HardwareDataInitializer(HardwareRepository hardwareRepository) {
        this.hardwareRepository = hardwareRepository;
    }

    @Override
    public void run(String... args) {

        addHardware(1, "FC（ファミリーコンピュータ）");
        addHardware(2, "FDS（ファミリーコンピュータ ディスクシステム）");
        addHardware(3, "SFC（スーパーファミコン）");
        addHardware(4, "GB（ゲームボーイ）");
        addHardware(5, "GBC（ゲームボーイカラー）");
        addHardware(6, "GBA（ゲームボーイアドバンス）");
        addHardware(7, "N64（Nintendo 64）");
        addHardware(8, "GC（ゲームキューブ）");
        addHardware(9, "Wii（Wii）");
        addHardware(10, "Wii U（Wii U）");
        addHardware(11, "DS（ニンテンドーDS）");
        addHardware(12, "3DS（ニンテンドー3DS）");
        addHardware(13, "Switch（Nintendo Switch）");
        addHardware(14, "Switch 2（Nintendo Switch 2）");

        addHardware(15, "PCE（PCエンジン）");
        addHardware(16, "PCE CD（PCエンジン CD-ROM²）");
        addHardware(17, "PC-FX（PC-FX）");

        addHardware(18, "Mark III（セガ・マークIII）");
        addHardware(19, "SMS（セガ・マスターシステム）");
        addHardware(20, "MD（メガドライブ）");
        addHardware(21, "SS（セガサターン）");
        addHardware(22, "DC（ドリームキャスト）");

        addHardware(23, "PS（PlayStation）");
        addHardware(24, "PS2（PlayStation 2）");
        addHardware(25, "PS3（PlayStation 3）");
        addHardware(26, "PS4（PlayStation 4）");
        addHardware(27, "PS5（PlayStation 5）");
        addHardware(28, "PSP（PlayStation Portable）");
        addHardware(29, "PS Vita（PlayStation Vita）");

        addHardware(30, "NEOGEO（ネオジオ）");
        addHardware(31, "NEOGEO CD（ネオジオCD）");
        addHardware(32, "NGP（ネオジオポケット）");

        addHardware(33, "WS（ワンダースワン）");

        addHardware(34, "3DO（3DO REAL）");
        
        addHardware(35, "その他");
    }

    private void addHardware(int sortOrder, String name) {

        Hardware hardware = hardwareRepository.findByName(name)
                .orElseGet(Hardware::new);

        hardware.setName(name);
        hardware.setSortOrder(sortOrder);
        hardware.setActive(true);

        hardwareRepository.save(hardware);
    }
}