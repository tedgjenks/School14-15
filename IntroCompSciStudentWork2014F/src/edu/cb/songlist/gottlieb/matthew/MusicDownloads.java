package edu.cb.songlist.gottlieb.matthew;

import java.util.*;

import edu.jenks.dist.cb.songlist.*;

public class MusicDownloads extends AbstractMusicDownloads {
	public abstract class AbstractMusicDownloads{
	}

	@Override
	public DownloadInfo getDownloadInfo(String title) {
		for(DownloadInfo title1 : getDownloadList()){
			if (title1.getTitle() == title){
				return title1;
			}
		}
		return null;
	}

	@Override
	public void updateDownloads(List<String> arg0) {
		// TODO Auto-generated method stub
		
	}

}
