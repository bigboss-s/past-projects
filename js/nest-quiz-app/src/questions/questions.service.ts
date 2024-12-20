import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Question } from './questions.entity';
import { Repository } from 'typeorm';
import { Answer } from '../answers/answer.entity';

@Injectable()
export class QuestionsService {
    constructor(
        @InjectRepository(Question) private questionsRepository: Repository<Question>,
        @InjectRepository(Answer) private answersRepository: Repository<Answer>,
    ) {}
    
    async findAll(): Promise<Question []>{
        return await this.questionsRepository.find({
            relations: ['answers']
        });
    }
}
