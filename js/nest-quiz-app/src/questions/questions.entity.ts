import { Field, Int, ObjectType, registerEnumType } from "@nestjs/graphql";
import { Answer } from "../answers/answer.entity";
import { Quiz } from "../quizzes/quiz.entity";
import { Column, Entity, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";


export enum QuestionType {
  SINGLE_ANSWER = 'SINGLE_ANSWER',
  MULTIPLE_ANSWER = 'MULTIPLE_ANSWER',
  ORDERED_ANSWER = 'ORDERED_ANSWER',
  OPEN_ANSWER = 'OPEN_ANSWER'
}

registerEnumType(QuestionType, {
    name: 'QuestionType',
});

@Entity()
@ObjectType()
export class Question{
    
    @PrimaryGeneratedColumn()
    @Field(type => Int)
    id: number;

    @Column()
    @Field()
    questionString: string;

    @Column({
        type: 'enum',
        enum: QuestionType,
        default: QuestionType.SINGLE_ANSWER
    })
    @Field(type => QuestionType)
    type: QuestionType;

    @Column()
    @Field(type => Int)
    quizId: number;

    @ManyToOne(() => Quiz, quiz => quiz.questions)
    @Field(type => Quiz)
    quiz: Quiz;

    @OneToMany(() => Answer, answer => answer.question, {
        cascade: true
    })
    @Field(type => [Answer])
    answers: Answer[];

}