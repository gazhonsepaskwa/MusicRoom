package be.nalebrun.musicroom.viewmodel

import androidx.lifecycle.ViewModel
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.repositories.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    val musicRepository: MusicRepository,
) : ViewModel() {

    val currentPosition = musicRepository.currentPosition
    val currentSong = musicRepository.currentSong
    val duration = musicRepository.duration
    val isPlaying = musicRepository.isPlaying
    val music = musicRepository.music
    val waitingList = musicRepository.waitingList


    // music control
    fun goToNextSong()              = musicRepository.goToNextSong()
    fun goToPreviousSong()          = musicRepository.goToPreviousSong()
    fun fetchMusicById(id : Int)    = musicRepository.fetchMusicById(id)
    fun seekTo(newPosition: Long)   = musicRepository.seekTo(newPosition)
    fun play()                      = musicRepository.play()
    fun pause()                     = musicRepository.pause()

    // waiting list control
    fun addSongToWaitingListNext(musicElem: MusicJson)         = musicRepository.addSongToWaitingListNext(musicElem)
    fun addSongToWaitingListEnd(musicElem: MusicJson)          = musicRepository.addSongToWaitingListEnd(musicElem)
    fun removeSongFromWaitingListByMusic(musicElem: MusicJson) = musicRepository.removeSongFromWaitingListByMusic(musicElem)
    fun removeSongFromWaitingListByIndex(index: Int)           = musicRepository.removeSongFromWaitingListByIndex(index)
    fun clearWaitingList()                                     = musicRepository.clearWaitingList()
    fun replaceWaitingList(newWaitingList: List<MusicJson>)    = musicRepository.replaceWaitingList(newWaitingList)

    override fun onCleared() {
        musicRepository.release()
    }
}