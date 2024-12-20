import { Module } from '@nestjs/common';
import { QuizzesService } from './quizzes.service';
import { QuizzesResolver } from './quizzes.resolver';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Quiz } from './quiz.entity';
import { QuestionsModule } from '../questions/questions.module';
import { AnswersModule } from '../answers/answers.module';

@Module({
  imports: [TypeOrmModule.forFeature([Quiz]), QuestionsModule, AnswersModule],
  providers: [QuizzesService, QuizzesResolver]
})
export class QuizzesModule {}
