export class PlaybackStateDto {
  isPlaying?: boolean;
  currentTime?: number;
  musicListIds?: number[];
  deviceId!: string;
}
