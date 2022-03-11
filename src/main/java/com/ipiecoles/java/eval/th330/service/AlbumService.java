package com.ipiecoles.java.eval.th330.service;

import com.ipiecoles.java.eval.th330.model.Album;
import com.ipiecoles.java.eval.th330.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;


    public Album creerAlbum(Album album) {
        if(albumRepository.findByTitle(album.getTitle()) != null){
            throw new EntityExistsException("Il existe déjà un album avec le nom " + album.getTitle());
        }
        return albumRepository.save(album);
    }

    public void deleteAlbum(Long id) {
        if(!albumRepository.existsById(id)){
            throw new EntityNotFoundException("Impossible de trouver l'album !");
        }
        albumRepository.deleteById(id);
    }
}
