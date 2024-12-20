import { Module } from '@nestjs/common';
import { AnswersService } from './answers.service';
import { AnswersResolver } from './answers.resolver';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Answer } from './answer.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Answer])],
  providers: [AnswersService, AnswersResolver],
  exports: [AnswersService, TypeOrmModule.forFeature([Answer])]
})
export class AnswersModule {}
