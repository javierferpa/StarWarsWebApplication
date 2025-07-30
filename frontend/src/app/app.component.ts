import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

/* Angular‑Material bits used in the template */
import { MatToolbarModule }      from '@angular/material/toolbar';
import { MatButtonModule }       from '@angular/material/button';
import { MatSlideToggleModule }  from '@angular/material/slide-toggle';
import { MatIconModule }         from '@angular/material/icon';

@Component({
  selector: 'sw-root',
  standalone: true,
  /* 👇 every module used in the template must be listed here
     because the component is stand‑alone */
  imports: [
    RouterLink, RouterLinkActive, RouterOutlet,
    MatToolbarModule, MatButtonModule, MatSlideToggleModule, MatIconModule
  ],
  templateUrl: './app.component.html',
  styleUrls : ['./app.component.scss']
})
export class AppComponent {

  /** keeps the current mode */
  darkMode = false;

  /** toggles a CSS class on <body> */
  toggleDarkMode(): void {
    this.darkMode = !this.darkMode;
    document.body.classList.toggle('dark-mode', this.darkMode);
  }
}
