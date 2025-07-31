export interface Planet {
  name:          string;
  rotationPeriod: string;
  orbitalPeriod:  string;
  diameter:       string;
  climate:        string;
  gravity:        string;
  terrain:        string;
  surfaceWater:   string;
  population:     number | null;
  residents:      string[];
  films:          string[];
  created:        string | null;
}
