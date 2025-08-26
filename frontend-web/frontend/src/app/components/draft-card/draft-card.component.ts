// src/app/components/draft/draft.component.ts
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Post } from '../../models/post.model';
import { DatePipe, SlicePipe } from '@angular/common';

@Component({
  selector: 'app-draft-card',
  standalone: true,
  imports: [DatePipe, SlicePipe],
  templateUrl: './draft-card.component.html',
  styleUrls: ['./draft-card.component.css'],
})
export class DraftCardComponent {
  @Input({ required: true }) post!: Post;
  @Output() publish = new EventEmitter<Post>();
  @Output() edit = new EventEmitter<Post>();

  publishing = false;

  onPublish() {
    if (this.publishing) return;
    this.publishing = true;
    this.publish.emit(this.post);
  }

  onEdit() {
    this.edit.emit(this.post);
  }
}
