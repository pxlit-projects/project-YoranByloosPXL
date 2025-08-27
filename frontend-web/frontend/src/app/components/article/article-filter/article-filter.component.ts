import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

export interface ArticleFilter {
  keyword?: string;
  author?: string;
  date?: string;
}

@Component({
  selector: 'app-article-filter',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './article-filter.component.html',
  styleUrls: ['./article-filter.component.css']
})
export class ArticleFilterComponent {
  private fb = inject(FormBuilder);

  @Output() search = new EventEmitter<ArticleFilter>();

  form = this.fb.group({
    keyword: [''],
    author: [''],
    dateOnly: ['']
  });

  private toLocalIso(dateOnly: string | null | undefined): string | undefined {
    if (!dateOnly) return undefined;
    return `${dateOnly}T00:00:00`;
  }

  submit() {
    const raw = this.form.getRawValue();
    const payload: ArticleFilter = {
      keyword: raw.keyword?.trim() || undefined,
      author: raw.author?.trim() || undefined,
      date: this.toLocalIso(raw.dateOnly || undefined)
    };
    this.search.emit(payload);
  }

  clear() {
    this.form.reset({ keyword: '', author: '', dateOnly: '' });
    this.search.emit({});
  }
}
