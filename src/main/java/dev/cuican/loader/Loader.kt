package dev.cuican.loader

import dev.cuican.staypro.launch.InitializationManager
import dev.cuican.loader.MixinCache.getMixins
import dev.cuican.loader.MixinCache.getRefMapFile
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket



fun launch() {

    val socket = Socket("127.0.0.1", 31212)
    val inputF = DataInputStream(socket.getInputStream())
    val outputF = DataOutputStream(socket.getOutputStream())
    AntiDump.check()
    outputF.writeUTF("[HWID]"+HWIDUtil.getEncryptedHWID("cuicanIsYourDaddy"))
    StayClassLoader(inputF)
    InitializationManager.init(MixinCache.refmapBytes.getRefMapFile(), MixinCache.mixinBytes.getMixins())
}

