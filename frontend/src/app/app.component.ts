import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

/* Angular‑Material bits used in the template */
import { MatToolbarModule }      from '@angular/material/toolbar';
import { MatButtonModule }       from '@angular/material/button';
import { MatIconModule }         from '@angular/material/icon';

@Component({
  selector: 'sw-root',
  standalone: true,
  /* 👇 every module used in the template must be listed here
     because the component is stand‑alone */
  imports: [
    RouterLink, RouterLinkActive, RouterOutlet,
    MatToolbarModule, MatButtonModule, MatIconModule
  ],
  templateUrl: './app.component.html',
  styleUrls : ['./app.component.scss']
})
export class AppComponent {

}
