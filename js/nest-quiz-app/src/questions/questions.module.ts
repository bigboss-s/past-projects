import { Module } from '@nestjs/common';
import { QuestionsService } from './questions.service';
import { QuestionsResolver } from './questions.resolver';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Question } from './questions.entity';
import { AnswersModule } from '../answers/answers.module';

@Module({
  imports: [TypeOrmModule.forFeature([Question]), AnswersModule],
  providers: [QuestionsService, QuestionsResolver],
  exports: [QuestionsService, TypeOrmModule.forFeature([Question])]
})
export class QuestionsModule {}
