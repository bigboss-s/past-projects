import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Answer } from './answer.entity';
import { Repository } from 'typeorm';

@Injectable()
export class AnswersService {
    constructor(@InjectRepository(Answer) private answersRepository: Repository<Answer>) {}

    async findAll(): Promise<Answer []>{
        return await this.answersRepository.find();
    }
}
